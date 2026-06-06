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
