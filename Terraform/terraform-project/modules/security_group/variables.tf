variable "cidr_blocks" {
  description = "CIDR blocks allowed for ingress/egress"
  type        = list(string)
  default     = ["0.0.0.0/0"]
}
variable "vpc_id" {
  description = "The ID of the VPC where the security group will be created"
  type        = string
}
