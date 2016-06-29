SELECT *
FROM (
  SELECT * FROM Coordinates
  WHERE tag != 'Apartment'
) as PointsExpApt
ORDER BY ST_MakePoint(longitude, latitude) <-> (SELECT ST_MakePoint(longitude, latitude)
                                                FROM Coordinates WHERE tag = 'Apartment')
LIMIT 3;
