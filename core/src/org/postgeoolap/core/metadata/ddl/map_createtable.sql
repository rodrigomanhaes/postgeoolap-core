CREATE TABLE map
(
  mapcode integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(255) NOT NULL,
  srid integer,
  schemacode integer NOT NULL,
  CONSTRAINT pk_map PRIMARY KEY (mapcode),
  CONSTRAINT fk_map_schema FOREIGN KEY (schemacode) REFERENCES esquema (schemacode)
)