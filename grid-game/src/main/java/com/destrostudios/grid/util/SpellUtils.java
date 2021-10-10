package com.destrostudios.grid.util;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.TargetableComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.buffs.BuffComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.perturn.CastsPerTurnComponent;
import com.destrostudios.grid.components.spells.range.AffectedAreaComponent;
import com.destrostudios.grid.components.spells.range.SpellAreaShape;
import com.destrostudios.grid.entities.EntityData;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpellUtils {
    // todo 6.) AP/MP steal components

    public static List<Integer> getAffectedTargetableEntities(int spellEntity, int sourceEntity, PositionComponent sourcePos,
                                                              PositionComponent clickedPos, EntityData entityData) {

        Set<PositionComponent> positionComponents = calculateAffectedPositions(sourcePos, clickedPos, entityData.getComponent(spellEntity, AffectedAreaComponent.class));
        int teamSource = entityData.getComponent(sourceEntity, TeamComponent.class).getTeam();

        return entityData.list(TargetableComponent.class).stream()
                .filter(e -> positionComponents.contains(entityData.getComponent(e, PositionComponent.class)))
                .sorted(entitiesToDamageSortComparator(clickedPos, entityData, teamSource))
                .collect(Collectors.toList());
    }

    private static Comparator<Integer> entitiesToDamageSortComparator(PositionComponent clickedPos, EntityData entityData, int teamSource) {
        return Comparator.comparingInt((Integer entity) -> {
            // 1. compare the distance to the clicked position
            PositionComponent posE = entityData.getComponent(entity, PositionComponent.class);
            int deltaXE1 = Math.abs(clickedPos.getX() - posE.getX());
            int deltaYE1 = Math.abs(clickedPos.getY() - posE.getY());
            return deltaXE1 + deltaYE1;

        }).thenComparingInt((Integer entity) -> {
            // 2. compare the team with the source team
            return -Math.abs(teamSource - entityData.getComponent(entity, TeamComponent.class).getTeam());

        }).thenComparingInt((Integer entity) -> {
            // 3. compare delta x to clicked pos
            PositionComponent posE = entityData.getComponent(entity, PositionComponent.class);
            return Math.abs(clickedPos.getX() - posE.getX());

        }).thenComparingInt((Integer entity) -> {
            // 4. compare delta y to clicked pos
            PositionComponent posE = entityData.getComponent(entity, PositionComponent.class);
            return Math.abs(clickedPos.getY() - posE.getY());

        }).thenComparingInt(Integer::intValue);
    }

    public static List<Integer> getAffectedWalkableEntities(int spellEntity, PositionComponent sourcePos, PositionComponent clickedPos, EntityData entityData) {
        Set<PositionComponent> positionComponents = calculateAffectedPositions(sourcePos, clickedPos, entityData.getComponent(spellEntity, AffectedAreaComponent.class));
        return entityData.list(WalkableComponent.class).stream()
                .filter(e -> positionComponents.contains(entityData.getComponent(e, PositionComponent.class)))
                .collect(Collectors.toList());
    }

    public static Set<PositionComponent> calculateAffectedPositions(PositionComponent sourcePos, PositionComponent clickedPos, AffectedAreaComponent component) {
        Set<PositionComponent> result = new LinkedHashSet<>();
        if (component == null) {
            return result;
        }

        int maxImpact = component.getMaxImpact();
        int minImpact = component.getMinImpact();
        int yPos = clickedPos.getY();
        int xPos = clickedPos.getX();

        if (component.getShape() == SpellAreaShape.SINGLE) {
            return Set.of(clickedPos);

        } else if (component.getShape() == SpellAreaShape.LINE) {
            return calculateAffectedPosEntitiesForLine(sourcePos, clickedPos, maxImpact, minImpact, yPos, xPos);

        } else {
            result.addAll(getBaseSquare(clickedPos, maxImpact));

            if (component.getShape() == SpellAreaShape.PLUS) {
                result.removeIf(pos -> !(pos.getX() == xPos || pos.getY() == yPos));
                result.removeAll(getBaseSquare(clickedPos, minImpact - 1));

            } else if (component.getShape() == SpellAreaShape.DIAMOND) {
                result.removeIf(pos -> Math.abs(pos.getX() - xPos) + Math.abs(pos.getY() - yPos) > maxImpact
                        || Math.abs(pos.getX() - xPos) + Math.abs(pos.getY() - yPos) < minImpact);
            } else if (component.getShape() == SpellAreaShape.SQUARE) {
                result.removeAll(getBaseSquare(clickedPos, minImpact - 1));
            }
        }
        return result;
    }

    public static List<PositionComponent> getBaseSquare(PositionComponent pos, int impact) {
        // add big square
        List<PositionComponent> resultTmp = new ArrayList<>();

        for (int y = pos.getY() - impact; y <= pos.getY() + impact; y++) {
            for (int x = pos.getX() - impact; x <= pos.getX() + impact; x++) {
                resultTmp.add(new PositionComponent(x, y));
            }
        }
        return resultTmp;
    }

    private static Set<PositionComponent> calculateAffectedPosEntitiesForLine(PositionComponent sourcePos, PositionComponent clickedPos, int maxImpact, int minImpact, int yPos, int xPos) {
        if (sourcePos == null) {
            return Sets.newHashSet(clickedPos);
        }

        int signumY = (int) Math.signum(clickedPos.getY() - sourcePos.getY());
        Function<Integer, Boolean> testY = signumY < 0
                ? y -> y >= yPos - maxImpact
                : y -> y <= yPos + maxImpact;

        int signumX = (int) Math.signum(clickedPos.getX() - sourcePos.getX());
        Function<Integer, Boolean> testX = signumX < 0
                ? x -> x >= xPos - maxImpact
                : x -> x <= xPos + maxImpact;

        Set<PositionComponent> result = new LinkedHashSet<>();

        if (Math.abs(sourcePos.getX() - clickedPos.getX()) < Math.abs(sourcePos.getY() - clickedPos.getY())) {
            int signum = (int) Math.signum(clickedPos.getY() - sourcePos.getY());

            for (int y = yPos + signumX * minImpact; testY.apply(y); y += signum) {
                result.add(new PositionComponent(xPos, y));
            }

        } else if (Math.abs(sourcePos.getX() - clickedPos.getX()) > Math.abs(sourcePos.getY() - clickedPos.getY())) {
            // from left or right
            for (int x = xPos + signumX * minImpact; testX.apply(x); x += signumX) {
                result.add(new PositionComponent(x, yPos));
            }

        } else if (signumY != 0 && signumX != 0) {
            // diagonal
            for (int x = xPos + signumX * minImpact, y = yPos + signumY * minImpact; testX.apply(x) && testY.apply(y); x += signumX, y += signumY) {
                result.add(new PositionComponent(x, y));
            }
        }
        return result;
    }

    public static int calculateTargetEntity(int x, int y, EntityData data) {
        Optional<Integer> targetEntity = data.list(PositionComponent.class).stream()
                .filter(e -> data.getComponent(e, PositionComponent.class).getX() == x
                        && data.getComponent(e, PositionComponent.class).getY() == y)
                .min((e1, e2) -> Boolean.compare(data.hasComponents(e2, TargetableComponent.class), data.hasComponents(e1, TargetableComponent.class)));
        return targetEntity.orElse(-1);
    }

    public static <E extends BuffComponent> int getBuffAmount(int spellEntity, int playerEntity, EntityData data, Class<E> classz) {
        int buffPlayer = data.hasComponents(playerEntity, classz)
                ? data.getComponent(playerEntity, classz).getBuffAmount()
                : 0;
        List<Integer> spellBuffEntities = data.hasComponents(spellEntity, BuffsComponent.class)
                ? data.getComponent(spellEntity, BuffsComponent.class).getBuffEntities()
                : new ArrayList<>();
        int buffSpell = spellBuffEntities.stream()
                .flatMap(spellBuff -> data.getComponents(spellBuff).stream())
                .filter(classz::isInstance)
                .map(spellBuff -> (BuffComponent) spellBuff)
                .mapToInt(BuffComponent::getBuffAmount)
                .sum();
        return buffPlayer + buffSpell;
    }


    public static boolean isPositionIsFree(EntityData entityData, PositionComponent newPosition, int entity) {
        List<Integer> newPositionEntities = new ArrayList<>(entityData.findEntitiesByComponent(newPosition));
        // TODO: do we really need to explicitly remove entity here? it will only block itself if we try to move it to its own position.
        newPositionEntities.remove((Integer) entity);
        boolean collidesWithOtherPlayer = newPositionEntities.stream()
                .anyMatch(e -> entityData.hasComponents(e, PlayerComponent.class));
        boolean collidesWithObstacle = newPositionEntities.stream()
                .anyMatch(e -> entityData.hasComponents(e, ObstacleComponent.class));
        boolean isWalkableField = newPositionEntities.stream()
                .anyMatch(e -> entityData.hasComponents(e, WalkableComponent.class));

        return isWalkableField && !collidesWithOtherPlayer && !collidesWithObstacle;
    }

    public static PositionComponent getDisplacementGoal(EntityData entityData, int entity, PositionComponent from, Direction direction, int steps) {
        PositionComponent result = from;
        for (int i = 1; i <= steps; i++) {
            PositionComponent next = new PositionComponent(from.getX() + i * direction.getDeltaX(), from.getY() + i * direction.getDeltaY());
            if (!isPositionIsFree(entityData, next, entity)) {
                break;
            }
            result = next;
        }
        return result;
    }

    public static boolean isCastable(int casterEntity, int spellEntity, EntityData entityData) {
        return isCostPayable(casterEntity, spellEntity, entityData) && maxCastsNotReached(spellEntity, entityData);
    }

    public static boolean maxCastsNotReached(int spellEntity, EntityData entityData) {
        CastsPerTurnComponent component = entityData.getComponent(spellEntity, CastsPerTurnComponent.class);
        return component == null || component.getCastsThisTurn() != component.getMaxCastsPerTurn();
    }

    public static boolean isCostPayable(int casterEntity, int spellEntity, EntityData entityData) {
        CostComponent cost = entityData.getComponent(spellEntity, CostComponent.class);
        AttackPointsComponent apComp = entityData.getComponent(casterEntity, AttackPointsComponent.class);
        MovementPointsComponent mpComp = entityData.getComponent(casterEntity, MovementPointsComponent.class);
        HealthPointsComponent hpComp = entityData.getComponent(casterEntity, HealthPointsComponent.class);
        return apComp.getAttackPoints() >= cost.getApCost() && mpComp.getMovementPoints() >= cost.getMpCost() && hpComp.getHealth() >= cost.getHpCost();
    }

    public static Direction directionForDelta(PositionComponent from, PositionComponent to) {
        return directionForDelta(to.getX() - from.getX(), to.getY() - from.getY());
    }

    public static Direction directionForDelta(int deltaX, int deltaY) {
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            deltaY = 0;
        } else if (Math.abs(deltaX) < Math.abs(deltaY)) {
            deltaX = 0;
        }

        int signX = Integer.signum(deltaX);
        int signY = Integer.signum(deltaY);
        for (Direction direction : Direction.values()) {
            if (direction.getDeltaX() == signX && direction.getDeltaY() == signY) {
                return direction;
            }
        }
        throw new AssertionError();
    }

}
