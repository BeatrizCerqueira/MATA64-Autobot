%original file at C:/robocode/Prolog.pl

less(A,B) :- A < B.
isEnemyClose(EnemyDistance, LimitDistance) :- less(EnemyDistance, LimitDistance).
