docker stop sk-openapi
docker rm   sk-openapi
docker rmi  sk-openapi

docker build -t sk-openapi .
docker run -d -it -p 10000:10000 --name=sk-openapi --network host sk-openapi