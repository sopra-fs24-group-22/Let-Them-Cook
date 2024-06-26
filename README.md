<p>
  <img src="client/src/assets/img/logo.png" alt="Let Them Cook Logo" width="216" height="40">
</p>

## Introduction

[Let Them Cook](https://let-them-cook.com) is a user-friendly web application that connects people from around the globe, enabling them to share homemade recipes and cook together in real time from their own kitchens. This platform fosters a sense of community and collaboration, making cooking a shared, interactive experience.

The global pandemic has reshaped our lives, driving many of us into our homes and disrupting our dining experiences. In response to this, our team developed "Let Them Cook" to bring people together through cooking, whether during lockdowns or as a way for friends and influencers to connect over a meal, no matter the distance. We believe that people cook better together!

On [Let-Them-Cook.com](https://let-them-cook.com), users—whom we call chefs—can create profiles to share their recipes or learn from others. Recipes and chefs are rated using a star system, making it easy to find promising cooking sessions. Distinct cooking steps are crucial for group cooking, ensuring a seamless experience compared to traditional plain-text recipes.

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

## Local development

In order to develop the application locally, you need to follow the steps below:

### MongoDB

To run the application locally, you need to have a MongoDB instance running. You can either install MongoDB locally or use a cloud service like MongoDB Atlas. For more information follow the [Official Guide](https://www.mongodb.com/docs/manual/installation/) depending on your development platform.

### Build server

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
./gradlew bootRun
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

Create an env file and set all variables

```
cp src/env_template.ts src/env.ts
```

The file `client/src/env.ts` then should look as follows:

```
export const ENV = {
  MAX_TEXT_INPUT_LENGTH: 50,      // Max length for text inputs
  MIN_NUMBER_MINUTES_LENGTH: 1,   // Min value for number inputs
  MAX_NUMBER_MINUTES_LENGTH: 500, // Max value for number inputs
  VIDEOSDK_API_TOKEN: "Your Video SDK API Token Here",
};
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

Install the proxy dependencies

```
npm install
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

## Deploying to production
Deploying to production is made easy using CI/CD pipelines and pre-written scripts.

In the scripts/ folder, you'll find the build.sh script. This script builds the backend and frontend for production, and puts them together correctly. It then creates a docker image based on the image specification found in the Dockerfile.

This script is run automatically by Github Actions when pushing to the master branch. In the .github/workflows, you'll find the deploy.yml pipeline config. This pipeline runs the build script, builds a docker image, pushes the docker image to a remote Docker registry in the cloud, and finally automatically updates the Google Cloud Run deployment by swapping the image to the new version and restarting the service.

## Illustrations

### Home Screen

This is the first screen that users see after they have logged themselves in. They are greeted with a dashboard that shows them their upcoming sessions they are signed up for, the next sessions that are available to join, as well as the newest recipes that have been added to the platform.

![Home Screen](img/home.png)

### Recipe Screen

Here users can view all recipes that have been created on the platform and also give them a 5-star rating. They can filter the recipes with various filter such as who created the recipe. They can add a recipe to their cookbook, which they can also access from this page. 

![Recipe Screen](img/recipe.png)

Recipes can also be created by the user. They can add a title, ingredients, preparation time, and steps to the recipe. They can also choose if the recipe should be public or private.

![Create Recipe](img/recipe_creation.png)

### Session Screen

Here users can view all sessions that are coming up and send a request for participation to the host user if they wish to participate in that session. They can filter the sessions with various filters such as the host of the session or the start date. Users can also see the sessions they have requested participation to or are the host of in the "My Sessions" Section

![Session Screen](img/session.png)

They can also create a session themselves, where they can choose a recipe to cook during the session, the start time and the maximum number of participants. Requests are also managed on this screen.

![Create Session](img/session_creation.png)

Once the session has started, the user will be able to see the stream of the host, and both host and participants can see every user's progress of the recipe on the left side of the screen.

![Session Stream](img/livestream.png)

### Chef Screen

Here users can view all chefs that are on the platform. Users can give each other ratings and they can also click on the name of the chef and see all the recipes they have created.

![Chef Screen](img/chef.png)

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

- **Cedric von Rauscher** - [Github](https://github.com/cedric-vr)

## License

[MIT License](LICENSE)
