# Let Them Cook

<p>
  <img src="client/public/logo.svg" alt="Let Them Cook Logo" width="200" height="200">
</p>

## Introduction

"Let Them Cook" is a user-friendly web application that connects people from around the globe, enabling them to share homemade recipes and cook together in real time from their own kitchens. This platform fosters a sense of community and collaboration, making cooking a shared, interactive experience.

The global pandemic has reshaped our lives, driving many of us into our homes and disrupting our dining experiences. In response to this, our team developed "Let Them Cook" to bring people together through cooking, whether during lockdowns or as a way for friends and influencers to connect over a meal, no matter the distance. We believe that people cook better together!

On Let-Them-Cook.com, users—whom we call chefs—can create profiles to share their recipes or learn from others. Recipes and chefs are rated using a star system, making it easy to find promising cooking sessions. Distinct cooking steps are crucial for group cooking, ensuring a seamless experience compared to traditional plain-text recipes.

When creating a recipe, chefs can detail each step, which is then stored in their personal online cookbook and made available for group cooking sessions. During a session, the host chef broadcasts live, guiding participants through each step. Participants can mark tasks as completed, allowing the host to adjust the pace for an optimal experience.

"Let Them Cook" inspires people to learn new dishes and improve their cooking skills, all while fostering collaboration and creating good-smelling homes.

## Technologies

- [Springboot](https://spring.io/) - Java framework to create micro services and web applications
- [videoSDK](https://www.videosdk.live/) - API for live streaming
- [Gradle](https://gradle.org/) - Automated building and management tool
- [MongoDB](https://www.mongodb.com/) - Database
- [React](https://reactjs.org/docs/getting-started.html) - Javascript library
- [Github Projects](https://github.com/explore) - Project Management
- [Google Cloud](https://cloud.google.com/) - Deployment
- [SonarCloud](https://sonarcloud.io/) - Test Coverage & Feedback of code quality

## High-Level Components

### User

Users are the main actors in "Let Them Cook". They can create an account, log in, and create, view, and join cooking sessions. Users can also create, view, and rate recipes as well as rate other users (Also called chefs).

[User Class](https://github.com/sopra-fs24-group-22/Let-Them-Cook/blob/main/server/src/main/java/com/letthemcook/user/User.java)

[User Service](https://github.com/sopra-fs24-group-22/Let-Them-Cook/blob/main/server/src/main/java/com/letthemcook/user/UserService.java)

### Recipe

Recipes form the backbone of "Let Them Cook". Users can create, view, share and rate recipes. They are formed by a recipe title, a list of ingredients, a list of steps to prepare the dish, the preparation time in minutes, and users can select if they want their recipe to be public or private.

[Recipe Class](https://github.com/sopra-fs24-group-22/Let-Them-Cook/blob/main/server/src/main/java/com/letthemcook/recipe/Recipe.java)

[Recipe Service](https://github.com/sopra-fs24-group-22/Let-Them-Cook/blob/main/server/src/main/java/com/letthemcook/recipe/RecipeService.java)

### Session

Sessions are the core feature of "Let Them Cook". Users can create, view, and join cooking sessions. Sessions are formed by a host chef, a list of participants, the start time of the stream and a recipe that will be cooked during the session. Users have to send requests and be accepted by the host so that they can be a part of the session. The host chef can then start the stream, and participants can cook along and mark steps as completed such that the chef and other participants can observe the progress of the session.

[Session Class](https://github.com/sopra-fs24-group-22/Let-Them-Cook/blob/main/server/src/main/java/com/letthemcook/session/Session.java)

[Session Service](https://github.com/sopra-fs24-group-22/Let-Them-Cook/blob/main/server/src/main/java/com/letthemcook/session/SessionService.java)

### videoSDK

In order for the livestream to work within sessions, we have implemented the videoSDK API within our application. This allows for the host chef to stream their cooking session live to the participants.

## Launch & Deployment

In order to develop the application locally, you need to follow the steps below:

### MongoDB

To run the application locally, you need to have a MongoDB instance running. You can either install MongoDB locally or use a cloud service like MongoDB Atlas. For more information follow the [Official Guide](https://www.mongodb.com/docs/manual/installation/) depending on your development platform.

### Gradle

You can use the local Gradle wrapper to build the application. The Gradle wrapper is already included in the project. To build the application, you can use the following command:

```
./gradlew build
```

More Information about [Gradle](https://gradle.org/docs/)

### Run Server

To run the server application, open the project in your IDE of choice and run the com.letthemcook application. The server will start on port 8080.

For the server to start, you also need to set the following environment variables:

```
MONGO_DB_URI="Your MongoDB Connection String"
MONGO_DB_NAME="The name of the database of your MongoDB instance"
VIDEOSDK_API_TOKEN="Your videoSDK API Token"
```

### Run Client

To run the client application, you need to run the following commands:

Change to the client directory

```
cd client
```

Install the dependencies

```
npm install
```

Start the client dev server

```
npm start
```

Build the client

```
npm run build
```

### Dev-proxy

Since we are using http-only cookies, you need to run the dev-proxy to avoid cross-origin errors. To run the dev-proxy, you need to start the frontend and backend server and then run the following commands:

Change to the dev-proxy directory from the project root directory

```
cd dev-proxy
```

Run the dev-proxy
    
```
npm start
```

You can now access the application through localhost:5000 and you will be greeted with the login page.

### Testing

To run the tests for the server, you can use the following commands:

Change to the server directory from the project root directory

```
cd server
```

Run the tests

```
./gradlew test
```

## Illustrations

TODO

## Roadmap

### Chat in Sessions

Currently, there is no chat functionality within a session stream. This would be a great feature to add so that participants can ask questions and interact directly with the host chef.

### Notifications

It would be helpful for users if they could receive notifications on the website or via email when a session they are participating in is about to start.


## Authors and Acknowledgment

### Contributors

- **Claudio Fleischmann** - [Github](https://github.com/c14ud3)
- **David Lanz** - [Github](https://github.com/Daveznal)
- **Gian Gyger** - [Github](https://github.com/giangyger)
- **Martin Fähnrich** - [Github](https://github.com/orgs/sopra-fs24-group-22/people/Ristafan)
- **Tristan Koning** - [Github](https://github.com/Alzameister)

### Supervision

- **Cedric** - [Github](https://github.com/cedric-vr)

## License

TODO
