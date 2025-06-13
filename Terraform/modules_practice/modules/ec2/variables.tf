variable "image_id" {
  description = "AMi id"
  type = string
}

variable "instance_type" {
  description = "Instance type"
  type = string
}

variable "user_data" {
  description = "Users data manual"
  type = string

}

variable "security_group_ids" {
  description = "List of security group IDs"
  type        = list(string)
  default     = []
}

