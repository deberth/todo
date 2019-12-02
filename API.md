# ToDo App for managing your own ToDo List 

##### Including our recent DLC- ToDo Subtasks!

How to start the application
---



API
---
### `GET /todos`
Returns all todos in the given data storage

| Parameter | Type | Description |
| :--- | :--- | :--- |
|  |  |  |

### `GET /todos/id`
Returns the Todo for the given id 1

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `id` | `integer` | **Required**. The id provided by the data storage |

### `POST /todos`
Create a new Todo for the provided body.  The ids will be created by the service.

| Parameter | Type | Description |
| :--- | :--- | :--- |
|  |  |  |

##### Sample body
```json
{
	"name": "TodoName",
	"description":"TodoDescription",
	"tasks": [
        {
            "name":"TaskName1",
            "description":"TaskDescription1"
        },
        {
            "name":"TaskName2",
            "description":"TaskDescription2"
        }
      ]
}
```
##### Sample response
```json
{
    "id": 1,
	"name": "TodoName",
	"description":"TodoDescription",
	"tasks": [
        {
            "id": 1,
            "name":"TaskName1",
            "description":"TaskDescription1"
        },
        {
            "id": 2,
            "name":"TaskName2",
            "description":"TaskDescription2"
        }
      ]
}
```

### `PUT /todos/id`
Create a new Todo for the provided body.  The ids will be created by the service.

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `id` | `integer` | **Required**. The id provided by the data storage |

##### Sample body
```json
{
	"name": "TodoName",
	"description":"TodoDescription",
	"tasks": [
        {
            "name":"TaskName1",
            "description":"TaskDescription1"
        },
        {
            "name":"TaskName2",
            "description":"TaskDescription2"
        }
      ]
}
```
##### Sample response
```json
{
    "id": 1,
	"name": "TodoName",
	"description":"TodoDescription",
	"tasks": [
        {
            "id": 1,
            "name":"TaskName1",
            "description":"TaskDescription1"
        },
        {
            "id": 2,
            "name":"TaskName2",
            "description":"TaskDescription2"
        }
      ]
}
```


Execute Tests
---
Just go ahead and use `mvn clean test` to execute all unit tests and the integration test 
##

@Author Dennis Berthold 2019