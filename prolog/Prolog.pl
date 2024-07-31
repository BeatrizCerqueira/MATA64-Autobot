% Utils
less(A,B) :- A < B.
lessOrEqual(A,B) :- A =< B.
equal(A,B) :- A = B.
greaterOrEqual(A,B) :- A >= B.
greater(A,B) :- A > B.

% Robocode
isEnemyClose(EnemyDistance, SafeDistance) :- less(EnemyDistance, SafeDistance).

isRadarTurnComplete(RadarTurnRemaining) :- equal(RadarTurnRemaining, 0.0).

shouldFire( Energy, Consts_MIN_LIFE_TO_FIRE) :-
    greater(Energy, Consts_MIN_LIFE_TO_FIRE).

isInXMargin(X, XLimit, Consts_WALL_MARGIN) :- XAbs is abs(X), less((XLimit - XAbs), Consts_WALL_MARGIN).

isInYMargin(Y, YLimit, Consts_WALL_MARGIN) :- YAbs is abs(Y), less((YLimit - YAbs), Consts_WALL_MARGIN).

isAtBorder(X, XLimit, Y, YLimit, Consts_WALL_MARGIN) :-
    isInXMargin(X, XLimit, Consts_WALL_MARGIN);
    isInYMargin(Y, YLimit, Consts_WALL_MARGIN).

isAtEdge(X, XLimit, Y, YLimit, Consts_WALL_MARGIN) :-
    isInXMargin(X, XLimit, Consts_WALL_MARGIN),
    isInYMargin(Y, YLimit, Consts_WALL_MARGIN).

isGunReady(Heat, Consts_GUN_HEAT_JUST_BEFORE_COOLING) :- less(Heat, Consts_GUN_HEAT_JUST_BEFORE_COOLING).

hasEnemyFired(EnergyDecreased, Rules_MIN_BULLET_POWER, Rules_MAX_BULLET_POWER) :-
   greaterOrEqual(EnergyDecreased, Rules_MIN_BULLET_POWER),
   lessOrEqual(EnergyDecreased, Rules_MAX_BULLET_POWER).
