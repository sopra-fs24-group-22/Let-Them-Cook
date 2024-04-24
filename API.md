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

# Recipe Endpoints

## Create recipeId

Used to create a new recipeId.

**URL** : `/api/recipeId`

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

## Get recipeId

Used to fetch a recipeId.

**URL** : `/api/recipeId/{id}`

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

## Delete recipeId

Used to delete a recipeId.

**URL** : `/api/recipeId/{id}`

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
**Condition** : Deleting a recipeId user does not own.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN "User is not allowed to delete this recipeId"
```

**Condition** : Deleting a recipeId that does not exist

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
- `title` - Title of the recipeId
- `creatorName` - Username of the user who created the recipeId
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

## Add recipeId to cookbook

Used to add a recipeId to the users personal cookbook.

**URL** : `/api/cookbook/recipeId/{id}`

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
**Condition** : Adding a recipeId that does not exist.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "Recipe not found"
```

**Condition** : Adding a recipeId that is already in the cookbook.

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

## Remove recipeId from cookbook

Used to remove a recipeId from the users personal cookbook.

**URL** : `/api/cookbook/recipeId/{id}`

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
**Condition** : Deleting a recipeId from cookbook that does not exist.

**Code** : `404 NOT FOUND`

**Content** :

```
404 NOT FOUND "Recipe not found"
```

**Condition** : Deleting a recipeId from cookbook that is not in the cookbook.

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
  - `recipeId` - ID of the recipeId used in the session
  - `sessionName` - Name of the session
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
      "roomId": "[(String) Room id]"
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

## Update checklistCount in session

**URL** : `/api/session/{id}/check`

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