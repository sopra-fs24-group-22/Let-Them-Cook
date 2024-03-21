cd client && npm run build && cd ../
rm -rf server/src/main/resources/static/*
cp -r client/build/* server/src/main/resources/static/
cd server && ./gradlew build && cd ../

docker build -t let-them-cook:latest .