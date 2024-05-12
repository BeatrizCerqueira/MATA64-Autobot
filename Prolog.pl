% Utils
less(A,B) :- A < B.
lessOrEqual(A,B) :- A =< B.
equal(A,B) :- A = B.
greaterOrEqual(A,B) :- A >= B.
greater(A,B) :- A > B.

% Robocode
isEnemyClose(EnemyDistance, SafeDistance) :- less(EnemyDistance, SafeDistance).

isRadarTurnComplete(RadarTurnRemaining) :- equal(RadarTurnRemaining, 0.0).

shouldFire(FirePowerToHit, Energy, Rules_MIN_BULLET_POWER, Rules_MAX_BULLET_POWER, Consts_MIN_LIFE_TO_FIRE) :-
    greaterOrEqual(FirePowerToHit, Rules_MIN_BULLET_POWER),
    lessOrEqual(FirePowerToHit, Rules_MAX_BULLET_POWER),
    greater(Energy, Consts_MIN_LIFE_TO_FIRE).

isInXMargin(X, XLimit, Consts_WALL_MARGIN) :- XAbs is abs(X), less((XLimit - XAbs), Consts_WALL_MARGIN).

isInYMargin(Y, YLimit, Consts_WALL_MARGIN) :- YAbs is abs(Y), less((YLimit - YAbs), Consts_WALL_MARGIN).

isGunReady(Heat) :- less(Heat, 0.3).

hasEnemyFired(EnergyDecreased) :- greater(EnergyDecreased, 0), lessOrEqual(EnergyDecreased, 3).

