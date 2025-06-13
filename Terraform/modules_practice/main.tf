provider "aws" {
  region = "us-east-2"

}


module "vpc" {
  source = "./modules/vpc"
  cidr_block     = var.cidr_block


}

module "security_group" {
  source = "./modules/sg"
  vpc_id = module.vpc.vpc_id   # Passing vpc_id from vpc module to security group module
}
module "ec2" {
  source        = "./modules/ec2"
  image_id      = var.image_id
  instance_type = var.instance_type
  user_data     = var.user_data
  security_group_ids = [module.security_group.security_group_id]
}

