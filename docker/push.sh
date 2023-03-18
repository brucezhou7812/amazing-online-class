#login docker image repository
docker login --username=aliyun6106954357 registry-vpc.cn-zhangjiakou.aliyuncs.com --password=Buptzw781@

echo "login docker image repository successfully."

#build the whole project
cd ../amazing-common
mvn install

#build the gateway
cd ../amazing-gateway
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag amazing-online-class/amazing-gateway:latest registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-gateway-service:v2
docker push registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-gateway-service:v2
echo "push amazing-gateway to the remote repository successfully."

#build coupon service
cd ../amazing-coupon-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag amazing-online-class/amazing-coupon-service:latest registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-coupon-service:v2
docker push registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-coupon-service:v2
echo "push amazing-coupon-service to the remote repository successfully."

#build the amazing-product-service
cd ../amazing-product-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag amazing-online-class/amazing-product-service:latest registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-product-service:v2
docker push registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-product-service:v2
echo "push amazing-product-service to the remote repository successfully."

#build the amazing-user-service
cd ../amazing-user-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag amazing-online-class/amazing-user-service:latest registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-user-service:v2
docker push registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-user-service:v2
echo "push amazing-user-service to the remote repository successfully."

#build the amazing-order-service
cd ../amazing-order-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag amazing-online-class/amazing-order-service:latest registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-order-service:v2
docker push registry-vpc.cn-zhangjiakou.aliyuncs.com/amazing-online-class/amazing-order-service:v2
echo "push amazing-order-service to the remote repository successfully."

echo "========Finished========="