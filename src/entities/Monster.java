package entities;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.IsFloor;

public class Monster extends Enemy {

    private int attackBoxOffsetX;

    public Monster(float x, float y) {
        super(x, y, MONSTER_WIDTH, MONSTER_HEIGHT, MONSTER);
        initHitbox(80, 30);
        initAttackBox(80, 200, 50);
    }


    public void update(int[][] lvlData, Player player) {
        MonsterUpdateAttackBox();
        updateBehavior(lvlData, player);
        updateAnimationTick();

    }


//    private void updateAttackBox() {
//        if (flipW() == -1) {
//            attackBox.x = (int) (hitbox.x - attackBoxOffsetX + 88 * Game.SCALE);
//        } else {
//            attackBox.x = (int) (hitbox.x - attackBoxOffsetX - 10 * Game.SCALE);
//        }
//        attackBox.y = (int) (hitbox.y - 20 * Game.SCALE);
//    }

    private void updateBehavior(int[][] lvlData, Player player) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir)
            updateInAir(lvlData);
        else {
            switch (state) {
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:
                    if (canSeePlayer(lvlData, player)) {
                        turnTowardPlayer(player);
                        if (isPlayerCloseForAttack(player))
                            newState(ATTACK);
                    }

                    moveMonster(lvlData);
                    break;
                case ATTACK:
                    if (aniIndex == 0)
                        attackChecked = false;
                    else if (aniIndex == 2) {
                            checkPlayerHit(attackBox, player);
//                        attackMove(lvlData);
                        break;
                    }
                case HIT:
                    break;
            }
        }
    }
//    private void attackMove(int[][] lvlData) {
//        float xSpeed = 0;
//
//        if (walkDir == LEFT)
//            xSpeed -= walkSpeed;
//        else
//            xSpeed += walkSpeed;
//
//        if (CanMoveHere(hitbox.x + xSpeed * 10, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
//            if (IsFloor(hitbox, xSpeed * 10, lvlData)) {
//                hitbox.x += xSpeed * 4;
//                return;
//            }
//        }
//        newState(IDLE);
//    }
//    public int flipX() {
//        if (walkDir == RIGHT)
//            return (int) (width - (45 * Game.SCALE));
//        else
//            return 0;
//    }
//
//    public int flipW() {
//        if (walkDir == RIGHT)
//            return -1;
//        else
//            return 1;
//    }
    }