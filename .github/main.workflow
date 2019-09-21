workflow "test" {
  on = "push"
  resolves = ["actions/checkout@v1"]
}

action "actions/checkout@v1" {
  uses = "actions/checkout@v1"
}
