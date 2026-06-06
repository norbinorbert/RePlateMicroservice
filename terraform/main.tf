resource "azurerm_resource_group" "res-0" {
  location = "westeurope"
  name     = "replate-rg"
}
resource "azurerm_kubernetes_cluster" "res-1" {
  automatic_upgrade_channel    = "patch"
  dns_prefix                   = "replate-cluster-dns"
  image_cleaner_enabled        = true
  image_cleaner_interval_hours = 168
  location                     = "austriaeast"
  name                         = "replate-cluster"
  oidc_issuer_enabled          = true
  resource_group_name          = azurerm_resource_group.res-0.name
  workload_identity_enabled    = true
  default_node_pool {
    auto_scaling_enabled = true
    max_count            = 2
    min_count            = 1
    name                 = "agentpool"
    upgrade_settings {
      max_surge = "10%"
    }
  }
  identity {
    type = "SystemAssigned"
  }
  maintenance_window_auto_upgrade {
    day_of_week = "Sunday"
    duration    = 8
    frequency   = "Weekly"
    interval    = 1
    start_time  = "00:00"
    utc_offset  = "+00:00"
  }
  maintenance_window_node_os {
    day_of_week = "Sunday"
    duration    = 8
    frequency   = "Weekly"
    interval    = 1
    start_time  = "00:00"
    utc_offset  = "+00:00"
  }
}
resource "azurerm_kubernetes_cluster_node_pool" "res-2" {
  auto_scaling_enabled  = true
  kubernetes_cluster_id = azurerm_kubernetes_cluster.res-1.id
  max_count             = 2
  min_count             = 1
  mode                  = "System"
  name                  = "agentpool"
  upgrade_settings {
    max_surge = "10%"
  }
}

resource "time_sleep" "wait_for_cluster" {
  depends_on = [azurerm_kubernetes_cluster.res-1]
  create_duration = "30s"
}

resource "kubectl_manifest" "namespace" {
  yaml_body  = file("${path.module}/../k8s/namespace.yaml")
  depends_on = [time_sleep.wait_for_cluster]
}

resource "kubectl_manifest" "secret" {
  yaml_body  = file("${path.module}/../k8s/secret.yaml")
  depends_on = [kubectl_manifest.namespace]
}

resource "kubectl_manifest" "configmap" {
  yaml_body  = file("${path.module}/../k8s/configmap.yaml")
  depends_on = [kubectl_manifest.secret]
}

resource "kubectl_manifest" "mysql" {
  yaml_body  = file("${path.module}/../k8s/mysql.yaml")
  depends_on = [kubectl_manifest.configmap]
}

resource "time_sleep" "wait_for_mysql" {
  depends_on = [kubectl_manifest.mysql]
  create_duration = "30s"
}

resource "kubectl_manifest" "auth_service" {
  yaml_body  = file("${path.module}/../k8s/auth-service.yaml")
  depends_on = [time_sleep.wait_for_mysql]
}

resource "kubectl_manifest" "filter_service" {
  yaml_body  = file("${path.module}/../k8s/filter-service.yaml")
  depends_on = [kubectl_manifest.auth_service]
}

resource "kubectl_manifest" "listing_service" {
  yaml_body  = file("${path.module}/../k8s/listing-service.yaml")
  depends_on = [kubectl_manifest.filter_service]
}

data "http" "ingress_nginx" {
  url = "https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml"
}

resource "kubectl_manifest" "ingress_nginx" {
  yaml_body  = data.http.ingress_nginx.response_body
  depends_on = [kubectl_manifest.listing_service]
}

resource "kubectl_manifest" "ingress" {
  yaml_body  = file("${path.module}/../k8s/ingress.yaml")
  depends_on = [kubectl_manifest.ingress_nginx]
}
