package entities;

import main.Game;

import java.awt.geom.Rectangle2D;

import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.GRAVITY;
import static utilz.HelpMethods.*;

public abstract class Enemy extends Entity {

    protected int enemyType;
    protected boolean firstUpdate = true;
    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = Game.TILES_SIZE;
    protected boolean active = true;
    protected boolean attackChecked;
    protected int attackBoxOffsetX;
    public static int count = 0;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        this.state = IDLE;
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;
        this.walkSpeed = 0.52f * Game.SCALE;
    }

    protected void updateAttackBox() {
        attackBox.x = hitbox.x - attackBoxOffsetX;
        attackBox.y = hitbox.y;
    }

    protected void updateAttackBoxFlip() {
        if (walkDir == RIGHT)
            attackBox.x = hitbox.x + hitbox.width;
        else
            attackBox.x = hitbox.x - attackBoxOffsetX;

        attackBox.y = hitbox.y;
    }

    protected void MinotaurUpdateAttackBox() {
        if (flipW() == 1) {
            attackBox.x = (int) (hitbox.x - attackBoxOffsetX + 40 * Game.SCALE);
        } else {
            attackBox.x = hitbox.x - attackBoxOffsetX;
        }
        attackBox.y = (int) (hitbox.y + 3 * Game.SCALE);
    }

    protected void MonsterUpdateAttackBox() {
        if (flipW() == -1) {
            attackBox.x = (int) (hitbox.x - attackBoxOffsetX + 110 * Game.SCALE);
        } else {
            attackBox.x = (int) (hitbox.x - attackBoxOffsetX - 110 * Game.SCALE);
        }
        attackBox.y = (int) (hitbox.y - 100 * Game.SCALE);
    }

    protected void initAttackBox(int w, int h, int attackBoxOffsetX) {
        attackBox = new Rectangle2D.Float(x, y, (int) (w * Game.SCALE), (int) (h * Game.SCALE));
        this.attackBoxOffsetX = (int) (Game.SCALE * attackBoxOffsetX);
    }

    protected void firstUpdateCheck(int[][] lvlData) {
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;

        firstUpdate = false;
    }

    protected void updateInAir(int[][] lvlData) {
        if (enemyType == BRINGER_OF_DEATH) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += GRAVITY;
            } else {
                inAir = false;
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                tileY = (int) ((hitbox.y + 27 * Game.SCALE) / Game.TILES_SIZE);
            }
            return;
        }
        if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
            hitbox.y += airSpeed;
            airSpeed += GRAVITY;
        } else {
            inAir = false;
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
            tileY = (int) (hitbox.y / Game.TILES_SIZE); // enemy tile Y

        }
    }

    protected void move(int[][] lvlData) {
        float xSpeed = 0;

        if (walkDir == LEFT) {
            if (enemyType == MONSTER) {
                xSpeed = (int) (-walkSpeed * Game.SCALE);
            } else
                xSpeed = -walkSpeed;
        } else {
            if (enemyType == MONSTER) {
                xSpeed = (int) (walkSpeed * Game.SCALE);
            } else
                xSpeed = walkSpeed;

        }
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
            if (IsFloor(hitbox, xSpeed, lvlData)) {
                hitbox.x += xSpeed;
                return;
            }

        changeWalkDir();
    }

    protected void turnTowardPlayer(Player player) {
        if (player.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    protected boolean canSeePlayer(int[][] lvlData, Player player) {
        int playerTileY;
        playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);
        // current y pos of player and change it to current tile Y
        if (playerTileY == tileY) {
            if (isPlayerInSight(player)) {
                if (IsSightClear(lvlData, hitbox, player.hitbox, tileY)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean isPlayerInSight(Player player) {
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance * 5;
    }

    protected boolean isPlayerCloseForAttack(Player player) {
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        switch (enemyType) {
            case CRABBY, MINOTAUR -> {
                return absValue <= attackDistance;
            }
            case SHARK, BRINGER_OF_DEATH, GOLEM -> {
                return absValue <= attackDistance * 2;
            }
//            case GOLEM -> {
//                return absValue <= attackDistance * 2;
//            }
            case MONSTER -> {
                if (walkDir == RIGHT)
                    return absValue <= attackDistance * 4;
                else return absValue <= attackDistance * 3;
            }
        }
        return false;
    }

    public void hurt(int amount) {
        currentHealth -= amount;
        if (currentHealth <= 0) {
            newState(DEAD);
        } else
            newState(HIT);
    }

    protected void checkPlayerHit(Rectangle2D.Float attackBox, Player player) {
        if (attackBox.intersects(player.hitbox)) {
            player.changeHealth(-GetEnemyDmg(enemyType));
        } else {
            if (enemyType == SHARK) {
                return;
            }
        }
        attackChecked = true;
    }


    protected void updateAnimationTick() {
        aniTick++;
        if ((state == HIT || state == ATTACK) && (enemyType == MINOTAUR)) ANI_SPEED = 10;
        if ((state == HIT) && (enemyType == GOLEM)) ANI_SPEED = 5;
        if ((state == ATTACK) && (enemyType == GOLEM)) ANI_SPEED = 10;

        if (enemyType == MONSTER) ANI_SPEED = 15;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(enemyType, state)) {
                if (enemyType == CRABBY || enemyType == SHARK || enemyType == MINOTAUR || enemyType == MONSTER || enemyType == BRINGER_OF_DEATH || enemyType == PINKSTAR || enemyType == GOLEM) {
                    aniIndex = 0;
                    switch (state) {
                        case ATTACK, HIT -> newState(IDLE);
                        case DEAD -> {
                            if (enemyType == CRABBY) count++;
                            if (enemyType == SHARK) count += 2;
                            if (enemyType == MINOTAUR) count += 3;
                            if (enemyType == GOLEM || enemyType == BRINGER_OF_DEATH) count += 5;
                            System.out.println("---" + count + "*-");
                            active = false;
                        }
                    }
                } else if (enemyType == PINKSTAR) {
                    if (state == ATTACK)
                        aniIndex = 3;
                    else {
                        aniIndex = 0;
                        if (state == HIT) {
                            newState(IDLE);
                        } else if (state == DEAD) {
                            count += 2;
                            System.out.println("---" + count + "*-");
                            active = false;
                        }

                    }
                }
            }
        }

        ANI_SPEED = 20;
    }

    protected void changeWalkDir() {
        if (walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    public void resetEnemy() {
        hitbox.x = x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(IDLE);
        active = true;
        airSpeed = 0;
    }

    public boolean isActive() {
        return active;
    }

    public int getCount() {
        return count;
    }

    protected int flipX() {

        if (walkDir == RIGHT) {
            if (enemyType == MONSTER) {
                return (int) (width - (38 * Game.SCALE));
            } else if (enemyType == MINOTAUR || enemyType == GOLEM) {
                return 0;
            }
            return width;
        } else {
            if (enemyType == MINOTAUR || enemyType == GOLEM) {
                return (int) (width - (30 * Game.SCALE));
            } else if (enemyType == MONSTER) {
                return (int) (-10 * Game.SCALE);
            } else if (enemyType == BRINGER_OF_DEATH) return (int) (-70 * Game.SCALE);
        }
        return 0;
    }

    protected int flipW() {
        if (walkDir == RIGHT) {
            if (enemyType == MINOTAUR || enemyType == GOLEM) {
                return 1;
            }
            return -1;
        } else if (enemyType == MINOTAUR || enemyType == GOLEM) {
            return -1;
        }
        return 1;
    }

}