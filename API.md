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
  "privacyStatus": "[PUBLIC/PRIVATE]"
}
```

**Data example**

```json
{
  "title": "[Butter chicken]",
  "checklist": ["Chop onions", "Cook chicken"],
    "ingredients": [ "1 Onion", "200g Chicken"],
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
  "title": "[Recipe title]",
  "checklist": ["List of checklist items"],
  "ingredients": ["List of ingredients"]
}
```

### Error Response

**Condition** : Invalid access token.

**Code** : `403 FORBIDDEN`

**Content** :

```
403 FORBIDDEN
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

# Session Endpoints

## Get Sessions

Used to fetch all sessions based on query params.

**URL** : `/api/sessions?{query params}`

**Query params**
  - `host` - ID of the host user
  - `recipe` - ID of the recipe used in the session
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
      "host": "[Host user id]",
      "recipe": "[Recipe id]",
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