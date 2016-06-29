-- CREATE EXTENSION postgis;
-- CREATE EXTENSION postgis_topology;
-- CREATE EXTENSION fuzzystrmatch;
-- CREATE EXTENSION postgis_tiger_geocoder;
-- #if you installed pgRouting extension, you can do
-- CREATE EXTENSION pgrouting;

CREATE TABLE Coordinates
(tag CHAR(30) NOT NULL,
longitude DOUBLE PRECISION NOT NULL,
latitude DOUBLE PRECISION NOT NULL,
PRIMARY KEY (tag));

INSERT INTO Coordinates(tag, latitude, longitude)
VALUES
('Apartment', 34.0223055556, -118.294083333),
('Exposition/Vermont', 34.0183611111, -118.291472222),
('Vermont/Jefferson', 34.0253888889, -118.291444444),
('Jefferson/Figueroa', 34.0218888889, -118.280111111),
('Figueroa/Exposition', 34.0183888889, -118.282333333),
('Cromwell Track and Field', 34.0221944444, -118.287583333),
('Leavey Library', 34.0217222222, -118.282944444),
('Tommy Trojan', 34.0205555556, -118.28541666),
('Hoose Library of Philosophy', 34.0188055556, -118.286583333);

-- Q4 - 1
SELECT ST_AsText(
ST_ConvexHull(ST_collect(points.geom)))
FROM ( SELECT ST_MakePoint(longitude,latitude) as geom FROM Coordinates) as points;

-- Q4 - 2
SELECT *
FROM (
  SELECT * FROM Coordinates
  WHERE tag != 'Apartment'
) as PointsExpApt
ORDER BY ST_MakePoint(longitude, latitude) <-> (SELECT ST_MakePoint(longitude, latitude)
                                                FROM Coordinates WHERE tag = 'Apartment')
LIMIT 3;
