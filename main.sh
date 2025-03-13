#!/bin/bash

AWS_REGION="us-east-1"
INSTANCE_TYPE="t2.micro"
AMI_ID="ami-08b5b3a93ed654d19"  # Amazon Linux 2 AMI
KEY_NAME="my-key"  # Change this to your existing key pair
SECURITY_GROUP_NAME="ec2-docker-sg"
INSTANCE_NAME="DockerInstance"
API_KEY="AIzaSyAilSjQ_55nmcWBBKvVpVkqDjThhLVQd1U"

# Step 1: Create a Security Group
echo "Creating security group..."
SECURITY_GROUP_ID=$(aws ec2 create-security-group \
  --group-name "$SECURITY_GROUP_NAME" \
  --description "Allow port 8080" \
  --region "$AWS_REGION" \
  --query 'GroupId' --output text)

# Step 2: Add inbound rule to allow port 8080
aws ec2 authorize-security-group-ingress \
  --group-id "$SECURITY_GROUP_ID" \
  --protocol tcp --port 8080 --cidr 0.0.0.0/0 \
  --region "$AWS_REGION"

echo "Security group created: $SECURITY_GROUP_ID"

# Step 3: Launch an EC2 Instance
echo "Launching EC2 instance..."
INSTANCE_ID=$(aws ec2 run-instances \
  --image-id "$AMI_ID" \
  --count 1 \
  --instance-type "$INSTANCE_TYPE" \
  --key-name "$KEY_NAME" \
  --security-group-ids "$SECURITY_GROUP_ID" \
  --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=$INSTANCE_NAME}]" \
  --query 'Instances[0].InstanceId' --output text \
  --region "$AWS_REGION")

echo "Instance launched: $INSTANCE_ID"

# Step 4: Wait for instance to be in running state
echo "Waiting for instance to be running..."
aws ec2 wait instance-running --instance-ids "$INSTANCE_ID" --region "$AWS_REGION"

# Get Public IP of the Instance
PUBLIC_IP=$(aws ec2 describe-instances \
  --instance-ids "$INSTANCE_ID" \
  --query 'Reservations[0].Instances[0].PublicIpAddress' --output text \
  --region "$AWS_REGION")

echo "Instance is running at: $PUBLIC_IP"

# Step 5: Install Docker & Create .env file
echo "Installing Docker and setting up environment file..."
ssh -o "StrictHostKeyChecking=no" -i "$KEY_NAME.pem" ec2-user@"$PUBLIC_IP" <<EOF
  sudo yum update -y
  sudo yum install -y docker git
  sudo systemctl start docker
  sudo systemctl enable docker
  git clone https://github.com/blog-service/blog-service.git
  cd blog-service
  echo "API_KEY=$API_KEY" | sudo tee /home/ec2-user/blog-service/.env > /dev/null
  sudo chmod 600 /home/ec2-user/blog-service/.env
  docker-compose --env-file /home/ec2-user/blog-service/.env up -d
EOF

echo "Docker installed and .env file created successfully on EC2 instance!"
echo "You can SSH into the instance using: ssh -i \"$KEY_NAME.pem\" ec2-user@$PUBLIC_IP"
