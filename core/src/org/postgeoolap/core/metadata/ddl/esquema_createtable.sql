CREATE TABLE esquema
(
  schemacode integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(50) NOT NULL,
  username varchar(50),
  password varchar(50),
  server varchar(50),
  map varchar(255),
  srid integer,
  CONSTRAINT pk_schema PRIMARY KEY (schemacode)
)