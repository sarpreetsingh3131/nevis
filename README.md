# Nevis Backend Home Task

## Instructions
### Run app
1. Start docker engine
2. Run `mvn spring-boot:run -Dspring-boot.run.profiles=local`
3. Swagger - http://localhost:8080/swagger-ui/index.html

### Run unit tests
1. Run `mvn test -Punit-tests`

### Run integration tests
1. Start docker engine
2. Run `mvn test -Pintegration-tests -Dspring.profiles.active=local`

## Example requests and responses
### Client
`POST http://localhost:8080/api/v1/clients`
```json lines
{
  // Payload
  "firstName": "john",
  "lastName": "noe",
  "email": "john.doe@neviswealth.com",
  "description": "an employee at nevis company",
  "socialLinks": [
    "https://linkend.com/in/john-noe"
  ]
}

{
  // Response
  "id": "d0b28d4c-736d-45f5-98df-681f12ab36df",
  "firstName": "john",
  "lastName": "noe",
  "email": "john.doe@neviswealth.com",
  "description": "an employee at nevis company",
  "socialLinks": [
    "https://linkend.com/in/john-noe"
  ]
}
```

```json lines
{
  // Payload
  "firstName": "sam",
  "lastName": "altman",
  "email": "sam.altman@openai.com",
  "description": "CEO of openai since 2019"
}

{
  // Response
  "id": "68cf1f30-fa49-4d52-8d04-fa9ad565e8f9",
  "firstName": "sam",
  "lastName": "altman",
  "email": "sam.altman@openai.com",
  "description": "CEO of openai since 2019",
  "socialLinks": null
}
```

```json lines
{
  // Payload
  "firstName": "mark",
  "lastName": "zuck",
  "email": "mark.zuck@meta.com",
  "description": "CEO of meta that owns products like facebook, instagram and whatsapp"
}

{
  // Response
  "id": "c90649ed-d74a-4d4a-949a-99af175e3b1e",
  "firstName": "mark",
  "lastName": "zuck",
  "email": "mark.zuck@meta.com",
  "description": "CEO of meta that owns products like facebook, instagram and whatsapp",
  "socialLinks": null
}
```

```json lines
{
  // Payload
  "firstName": "andy",
  "lastName": "lee",
  "email": "andy.lee5681@yahoo.com"
}

{
  // Response
  "id": "e28ffc9a-35e4-4c5a-9611-4c648ca01699",
  "firstName": "andy",
  "lastName": "lee",
  "email": "andy.lee5681@yahoo.com",
  "description": null,
  "socialLinks": null
}
```

### Document
`POST http://localhost:8080/api/v1/clients/{clientId}/documents`

```json lines
{
  // Payload (client: john noe)
  "title": "Payslip",
  "content": "A payslip of with date 31 March 2026 and total amount £4503.82. The employee name is John noe and employer name is Nevis."
}

{
  // Response
  "id": "8c2265ff-be3d-47cf-839c-21adec09a915",
  "clientId": "d0b28d4c-736d-45f5-98df-681f12ab36df",
  "title": "Payslip",
  "content": "A payslip of with date 31 March 2026 and total amount £4503.82. The employee name is John noe and employer name is Nevis.",
  "createdAt": "2026-06-01T14:58:05.682739Z"
}
```

```json lines
{
  // Payload (client: sam altman)
  "title": "Resarch paper",
  "content": "A research of current topics within the field of artificial intelligence. It overviews the current approaches and suggests new techniques to build smart systems that can act like human. The research is published on 13 Jan 2024."
}

{
  // Response
  "id": "41907401-2d25-4785-b2ad-5f37a4133f70",
  "clientId": "68cf1f30-fa49-4d52-8d04-fa9ad565e8f9",
  "title": "Resarch paper",
  "content": "A research of current topics within the field of artificial intelligence. It overviews the current approaches and suggests new techniques to build smart systems that can act like human. The research is published on 13 Jan 2024.",
  "createdAt": "2026-06-01T15:02:04.179182Z"
}
```
io.netty.handler.timeout.ReadTimeoutException

```json lines
{
  // Payload (client: andy lee)
  "title": "Council tax bill",
  "content": "An annual council tax for house no 59, Rimu street, London, UK. The monthly installement is £131.46."
}

{
  "id": "ff1ecb62-8229-49e1-9d0c-7577e7599a87",
  "clientId": "2efb68be-7207-4c76-afaa-b8e62f976d34",
  "title": "Council tax bill",
  "content": "An annual council tax for house no 59, Rimu street, London, UK. The monthly installement is £131.46.",
  "createdAt": "2026-06-01T15:05:34.354969Z"
}
```

```json lines
{
  // Payload (client: andy lee)
  "title": "TV license",
  "content": "A TV license for house no 47, Andresson street, London, UK. The monthly installement is £27.46."
}

{
  "id": "92e23031-f87e-4ae4-b43a-b063ba0e88eb",
  "clientId": "2efb68be-7207-4c76-afaa-b8e62f976d34",
  "title": "TV license",
  "content": "A TV license for house no 47, Andresson street, London, UK. The monthly installement is £27.46.",
  "createdAt": "2026-06-01T15:09:15.407511Z"
}
```

### Search
`GET http://localhost:8080/api/v1/clients/search?q=address proof&limit=2`

```json lines
{
  "clients": [],
  "documents": [
    {
      "score": 0.52,
      "id": "673b2599-0973-41de-b37b-23af258e4ec2",
      "title": "TV license",
      "summary": "Monthly TV licence payment for house no 47, Andresson Street, London, UK, at £27.46.",
      "createdAt": "2026-06-01T16:24:01.498208Z"
    },
    {
      "score": 0.46,
      "id": "da8586d0-7963-4932-925c-e7591ef61cca",
      "title": "Council tax bill",
      "summary": "Council tax bill for house no 59, Rimu street, London, with annual payment of £1,577.28 (£131.46 per month).",
      "createdAt": "2026-06-01T16:24:01.497643Z"
    }
  ]
}
```

`GET http://localhost:8080/api/v1/clients/search?q=nevis&limit=1`
```json lines
{
    "clients": [
        {
            "score": 0.74,
            "id": "60ce9e56-4961-4563-83a0-154b8d2f7583",
            "firstName": "john",
            "lastName": "noe",
            "email": "john.doe@neviswealth.com",
            "description": "an employee at nevis company"
        }
    ],
    "documents": []
}
```


