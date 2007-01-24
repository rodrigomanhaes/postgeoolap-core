CREATE TABLE dimension
(
  dimensioncode integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(50) NOT NULL,
  dimensionname varchar(50) NOT NULL,
  dimensiontype varchar(50),
  sqlcommand varchar(500),
  clause varchar(255),
  cubecode integer,
  tablecode integer,
  CONSTRAINT pk_dimension PRIMARY KEY (dimensioncode),
  CONSTRAINT fk_dimension_cube FOREIGN KEY (cubecode) REFERENCES cube (cubecode)
)