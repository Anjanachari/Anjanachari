provider "aws" {
  region = var.aws_region
}

module "vpc" {
  source = "./modules/vpc"
  cidr_block     = var.vpc_cidr_block
  azs            = var.azs
  public_subnets = var.public_subnets
  private_subnets = []

}

module "security_group" {
  source = "./modules/security_group"
  vpc_id = module.vpc.vpc_id  # Passing vpc_id from vpc module to security group module
}

module "ec2" {
  source = "./modules/ec2"
  subnet_id         = module.vpc.public_subnets[0]  # Ensure this is the correct subnet
  security_group_id = module.security_group.security_group_id  # Associate security group
  ami_id            = var.ami_id
  instance_type     = var.instance_type


}

