terraform {
  backend "local" {}

  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
      version = "4.58.0"
    }
    kubernetes = {
      source = "hashicorp/kubernetes"
      version = "~> 2.31"
    }
    kubectl = {
      source = "gavinbunney/kubectl"
      version = "~> 1.14"
    }
    time = {
      source = "hashicorp/time"
      version = "~> 0.9"
    }
    http = {
      source  = "hashicorp/http"
      version = "~> 3.0"
    }
  }
}
