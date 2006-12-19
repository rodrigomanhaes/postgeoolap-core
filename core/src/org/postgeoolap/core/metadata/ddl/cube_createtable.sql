CREATE TABLE cube
(
  cubecode integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(50) NOT NULL,
  schemacode integer,
  minimumaggregation integer,
  CONSTRAINT pk_cube PRIMARY KEY (cubecode),
  CONSTRAINT fk_cube_schema FOREIGN KEY (schemacode) REFERENCES esquema (schemacode)
)