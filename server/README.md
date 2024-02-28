# server

The main game server which handles WS connections and all the game state.

# Deployment

We deploy the server using the bash script in ops called [deploy-server](../ops/deploy-server). This is built as a 
docker image, pushed to ECR, and we have to manually bump the ECS Task revision for now.
