# Auth Endpoints

## Register

Used to register a new user.

**URL** : `/api/auth/register`

**Method** : `POST`

**Auth required** : NO

**Data constraints**

```json
{
  "email": "[valid email address]",
  "username": "[valid username]",
  "firstname": "[first name in plain text]",
  "lastname": "[last name in plain text]",
  "password": "[password in plain text]"
}
```

**Data example**

```json
{
  "email": "testUser@gmail.com",
  "username": "testUser",
  "firstName": "Test",
  "lastName": "User",
  "password": "Test123"
}
```

### Success Response

**Code** : `201 CREATED`

**Content example**

```json
{
  "accessToken": "93144b288eb1fdccbe46d6fc0f241a51766ecd3d",
  "refreshToken": "a2344b288eb1asd236fc0f241a51766ecd3d"
}
```

### Error Response

**Condition** : If 'email' already exists.

**Code** : `409 CONFLICT`

**Content** :

```
409 CONFLICT "Creating user failed because email already exists"
```

**Condition** : If 'username' already exists.

**Code** : `409 CONFLICT`

**Content** :

```
409 CONFLICT "Creating user failed because username already exists"
```

## Login

Used to login a user.

**URL** : `/api/auth/login`

**Method** : `POST`

**Auth required** : NO

**Data constraints**

```json
{
  "username": "[valid username]",
  "password": "[valid password]"
}
```

**Data example**

```json
{
  "username": "testUser@gmail.com",
  "password": "test123"
}
```

### Success Response

**Code** : `200 OK`

**Content example**

```json
{
  "accessToken": "93144b288eb1fdccbe46d6fc0f241a51766ecd3d",
  "refreshToken": "a2344b288eb1asd236fc0f241a51766ecd3d"
}
```

### Error Response

**Condition** : Invalid username / password combination.

**Code** : `401 UNAUTHORIZED`

**Content** :

```
401 UNAUTHORIZED "Bad credentials"
```

## Refresh

Used to fetch a new access token.

**URL** : `/api/auth/refresh`

**Method** : `GET`

**Auth required** : NO

**Data constraints**

```json
{
  "refreshToken": "[valid refresh token]"
}
```

**Data example**

```json
{
  "refreshToken": "a2344b288eb1asd236fc0f241a51766ecd3d"
}
```

### Success Response

**Code** : `200 OK`

**Content example**

```json
{
  "accessToken": "93144b288eb1fdccbe46d6fc0f241a51766ecd3d",
  "refreshToken": "a2344b288eb1asd236fc0f241a51766ecd3d"
}
```

### Error Response

**Condition** : Invalid refresh token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN "Invalid refresh token"
```

# User Endpoints

## Get user

Used to get own profile information.

**URL** : `/api/user/me`

**Method** : `GET`

**Auth required** : YES

### Success Response

**Code** : `200 OK`

**Content example**

```json
{
  "email": "[valid email address]",
  "username": "[valid username]",
  "firstname": "[first name in plain text]",
  "lastname": "[last name in plain text]",
  "id": "[user id]"
}
```

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Update user

Used to update own profile information.

**URL** : `/api/user/me`

**Method** : `PUT`

**Auth required** : YES

**Data constraints**

```json
{
  "email": "[valid email address]",
  "username": "[valid username]",
  "firstname": "[first name in plain text]",
  "lastname": "[last name in plain text]",
  "password": "[user password in plain text]"
}
```

**Data example**

```json
{
  "email": "TeamBackend.isch@besser.forever",
  "username": "Grüsse",
  "firstname": "Gehen",
  "lastname": "Raus",
  "password": "anDenAnderenK(onn)ing"
}
```

### Success Response

**Code** : `200 OK`




### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Delete user

Used to delete own profile.

**URL** : `/api/me`

**Method** : `DELETE`

**Auth required** : YES

### Success Response

**Code** : `204 NO CONTENT`

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Get Users

Used to fetch all users based on query params.

**URL** : `/api/users?{query params}`

**Query params**
- `username` - Username of the user
- `id` - ID of the user

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content**

```json
{
  "sessions": [
    {
      "username": "[username]",
      "id": "[user id]"
    }
  ]
}
```

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```


# Recipe Endpoints

## Create recipe

Used to create a new recipe.

**URL** : `/api/recipe`

**Method** : `POST`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

**Data constraints**

```json
{
  "title": "[Recipe title]",
  "checklist": ["List of checklist items"],
  "ingredients": ["List of ingredients"],
  "cookingTimeMin": "Integer: Cooking time in minutes",
  "privacyStatus": "[PUBLIC = 0 / PRIVATE = 1]"
}
```

**Data example**

```json
{
  "title": "[Butter chicken]",
  "checklist": ["Chop onions", "Cook chicken"],
  "ingredients": [ "1 Onion", "200g Chicken"],
  "cookingTimeMin": 30,
  "privacyStatus": 1
}
```

### Success Response

**Code** : `201 CREATED`

**No content**

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** : 

```
403 FORBIDDEN
```

## Get recipe

Used to fetch a recipe.

**URL** : `/api/recipe/{id}`

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content**
    
```json
{
  "creatorId": "[User id]",
  "creatorName": "[username]",
  "title": "[Recipe title]",
  "checklist": ["List of checklist items"],
  "ingredients": ["List of ingredients"],
  "cookingTimeMin": "Integer: Cooking time in minutes,"
}
```

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Put recipe

Used to update an existing recipe.

**URL** : `/api/recipe`

**Method** : `PUT`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

**Data constraints**

```json
{
  "id": "[recipe id]",
  "title": "[Recipe title]",
  "checklist": ["List of checklist items"],
  "ingredients": ["List of ingredients"],
  "cookingTimeMin": "Integer: Cooking time in minutes,",
  "privacyStatus": "[PUBLIC = 1 / PRIVATE = 0]"
}
```

**Data example**

```json
{
  "id": 1,
  "title": "Butter Chicken",
  "checklist": ["Chop Onions", "Cook chicken"],
  "ingredients": ["200g chicken", "1 Onion", "Clove of garlic"],
  "cookingTimeMin": 60
}
```

### Success Response

**Code** : `200 OK`

**No Content**

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

**Condition** : Recipe was not found.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "Recipe not found"
```

**Condition** : User tries to update a recipe they do not own.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN "User is not allowed to update this recipe"
```

## Delete recipe

Used to delete a recipe.

**URL** : `/api/recipe/{id}`

**Method** : `DELETE`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `204 NO CONTENT`

**Content**

```
204 NO CONTENT
```

### Error Response
**Condition** : Deleting a recipe user does not own.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN "User is not allowed to delete this recipe"
```

**Condition** : Deleting a recipe that does not exist

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "Recipe not found"
```

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Get recipes

Used to query for recipes.

**URL** : `/api/recipes?{query params}`

**Query params**
- `title` - Title of the recipe
- `creatorName` - Username of the user who created the recipe
- `cookingTimeMin` - The maximum cooking time in minutes

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content example**

```json
{
  [
    {
      "id": 1,
      "creatorId": 70,
      "title": "Butter Chicken",
      "checklist": [
        "Chop Onions",
        "cook chicken"
      ],
      "ingredients": [
        "200g chicken",
        "1 Onion",
        "Clove of garlic"
      ],
      "cookingTimeMin": 60,
      "privacyStatus": 1
    }
  ]
}
```

# Cookbook Endpoints

## Add recipe to cookbook

Used to add a recipe to the users personal cookbook.

**URL** : `/api/cookbook/recipe/{id}`

**Method** : `POST`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `201 CREATED`

**Content**

```
201 CREATED
```

### Error Response
**Condition** : Adding a recipe that does not exist.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "Recipe not found"
```

**Condition** : Adding a recipe that is already in the cookbook.

**Code** : `409 CONFLICT`

**Content** :

```
409 CONFLICT "Recipe already in cookbook"
```

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Remove recipe from cookbook

Used to remove a recipe from the users personal cookbook.

**URL** : `/api/cookbook/recipe/{id}`

**Method** : `DELETE`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `204 NO CONTENT`

**Content**

```
204 NO CONTENT
```

### Error Response
**Condition** : Deleting a recipe from cookbook that does not exist.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "Recipe not found"
```

**Condition** : Deleting a recipe from cookbook that is not in the cookbook.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "Recipe not in cookbook"
```

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Get personal cookbook

**URL** : `/api/cookbook/{id}`

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content**

```json
[
  {
    "id": 1,
    "creatorId": 1,
    "creatorName": "Trizzy",
    "title": "Butter Chicken",
    "checklist": [
      "Chop Onions",
      "cook chicken"
    ],
    "ingredients": [
      "200g chicken",
      "1 Onion",
      "Clove of garlic"
    ],
    "cookingTimeMin": 10,
    "privacyStatus": 1
  }
]
```

### Error Response
**Condition** : Getting a cookbook that does not belong to the user.

**Code** : `401 UNAUTHORIZED`

# Session Endpoints

## Get Sessions

Used to fetch all sessions based on query params.

**URL** : `/api/sessions?{query params}`

**Query params**
  - `hostId` - ID of the hostId user
  - `hostName` - Name of the host user
  - `recipeId` - ID of the recipeId used in the session
  - `recipeName` - Name of the recipe used in the session
  - `sessionName` - Name of the session
  - `date` - Sessions occurring after this date
  - `maxParticipantCount` - Maximum number of participants allowed in the session
  - `maxParticipants` - Maximum number of participants in the session
  - `minParticipants` - Minimum number of participants in the session
  - `limit` - Limit of sessions to fetch
  - `offset` - Offset of sessions to fetch
  - *More query params to be implemented*

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content**
    
```json
{
  "sessions": [
    {
      "id": "[Session id]",
      "hostId": "[Host user id]",
      "creatorName": "[Host username]",
      "recipeId": "[Recipe id]",
      "sessionName": "[Session name]",
      "maxParticipantCount": "[Max participant count]",
      "participants": ["List of participant user ids"],
      "date": "[Date of session]"
    }
  ]
}
```

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Post Session

Used to create a new session

**URL** : `/api/session`

**Method** : `POST`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `201 CREATED`

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Get Session

Used to get a specific session.

**URL** : `/api/session/{sessionId}`

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content**

```json
{
  "sessions": [
    {
      "sessionName": "[Session name]",
      "host": "[Host user id]",
      "recipe": "[Recipe id]",
      "maxParticipantCount": "[Max participant count]",
      "currentParticipantCount": "[Current participant count]",
      "participants": ["List of participant user ids"],
      "date": "[Date of session]"
    }
  ]
}
```

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Put recipe

Used to update an existing session.

**URL** : `/api/session`

**Method** : `PUT`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

**Data constraints**

```json
{
  "hostName": "[Host username]",
  "recipeId": "[Recipe id]",
  "recipeName": "[recipe name]",
  "sessionName": "[Session name]",
  "maxParticipantCount": "Integer: Maximum number of participants",
  "participants": "[List of participant user ids]",
  "date": "[Date of session]",
  "duration": "[Duration of session in minutes]"
}
```

**Data example**

```json
{
  "hostname": "Trizzy",
  "recipeId": 1,
  "recipeName": "Butter Chicken",
  "sessionName": "Test session",
  "maxParticipantCount": 5,
  "participants": [12, 23, 16],
  "date": "2012-04-23T18:25:43.511Z",
  "duration": 60
}
```

### Success Response

**Code** : `200 OK`

**No Content**

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

**Condition** : Session was not found.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "Recipe not found"
```

**Condition** : User tries to update a recipe they do not own.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN "User is not allowed to update this recipe"
```

## Delete a session

Used to delete a scheduled session.

**URL** : `/api/session/{sessionId}`

**Method** : `DELETE`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `204 NO CONTENT`

**Content**

```
204 NO CONTENT
```

### Error Response
**Condition** : Deleting a recipe from cookbook that is not in the cookbook.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "Session not found
```

**Condition** : Invalid access token.

**Code** : `401 UNAUTHORIZED`

**Content** :

```
401 UNAUTHORIZED "You are not authorized to delete this session"
```

## Get Session Credentials

Used to get credentials for a specific session.

**URL** : `/api/session/credentials/{sessionId}`

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content**

```json
{
  "sessions": [
    {
      "host": "[Host user id]",
      "roomId": "[(String) Room id]",
      "sessionId": "[Session id]",
      "recipeId": "[Recipe id]"
    }
  ]
}
```

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

**Condition** : User is not participant in session.

**Code** : `401 UNAUTHORIZED`

**Content** :

```
401 UNAUTHORIZED "You are not authorized to get credentials for this session"
```

**Condition** : Session room is full.

**Code** : ` 401 UNAUTHORIZED`

**Content** :

```
401 UNAUTHORIZED "This session is full"
```



## Update checklistCount in session

**URL** : `/api/session/{id}/checklist`

**Method** : `PUT`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

**Data constraints**

```json
{
  "stepIndex": "[Index of step in checklist]",
  "isChecked": "[Boolean]"
}
```

**Data example**

```json
{
    "stepIndex": 0,
    "isChecked": true
}
```

### Success Response

**Code** : `204 NO CONTENT`

### Error Response

**Condition** : User is not part of the session

**Code** : `401 UNAUTHORIZED`

**Condition** Session does not exist

**Code** : `404 NOT FOUND`


## Get checklistCount in session

**URL** : `/api/session/{id}/checklist`

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content**

```json
{
  "sessionId": "[Session id]",
  "recipeSteps": "[Total number of steps in recipe]",
  "currentStepValues": {
    "userId": ["Boolean values for each step"]
  }
}
```

**Content example**

```json
{
  "sessionId": 1,
  "recipeSteps": 3,
  "currentStepValues": {
    2: ["true", "false", "false"]
  }
}
```

*Note: Null Boolean values within currentStepValues means user has joined but not ticked off the index yet.*

### Error Response

**Condition** : User is not part of the session

**Code** : `401 UNAUTHORIZED`

**Condition** Session does not exist

**Code** : `404 NOT FOUND`

# Session Request Endpoints

## Post Session Request

Used to send a request to join a session.

**URL** : `/api/session_request/{sessionId}`

**Method** : `POST`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `201 CREATED`

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

**Condition** : User has already sent a request to this session.

**Code** : `409 CONFLICT`

**Content** :

```
409 CONFLICT "You have already sent a session request for this session"
```

## Post Session Request Accept

Used to accept a session request.

**URL** : `/api/session_request/{sessionId}/accept`

**Method** : `POST`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

**Data constraints**

```json
{
  "userId": "[The Id of the user who sent the request]"
}
```

**Data example**

```json
{
    "userId": 1
}
```

### Success Response

**Code** : `200 OK`

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

**Condition** : User has not sent a request to this session.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "The user has not sent a session request for this session"
```

**Condition** : The request of the user has already been processed.

**Code** : `409 CONFLICT`

**Content** :

```
409 CONFLICT "The request has already been accepted or rejected"
```

## Post Session Request Deny

Used to deny a session request.

**URL** : `/api/session_request/{sessionId}/deny`

**Method** : `POST`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

**Data constraints**

```json
{
  "userId": "[The Id of the user who sent the request]"
}
```

**Data example**

```json
{
    "userId": 1
}
```

### Success Response

**Code** : `200 OK`

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

**Condition** : User has not sent a request to this session.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "The user has not sent a session request for this session"
```

**Condition** : The request of the user has already been processed.

**Code** : `409 CONFLICT`

**Content** :

```
409 CONFLICT "The request has already been accepted or rejected"
```

## Get SessionRequests for session

Used to get all session requests for a specific session.

**URL** : `/api/session_request/{sessionId}`

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content**

```json
{
  "SingleSessionRequest": [
    {
      "sessionRequests": "[Hashmap of userIds and their status]"
    }
  ]
}
```

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```

## Get SessionRequests for user

Used to get all session requests for a specific user.

**URL** : `/api/session_request`

**Method** : `GET`

**Auth required** : YES

**Headers**
```
Authorization: Bearer [access token]
```

### Success Response

**Code** : `200 OK`

**Content**

```json
{
  "sessionRequest": [
    {
      "userId": "[userId]",
      "username": "[username]",
      "queueStatus": "[queueStatus]"
    }
  ]
}
```

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
```