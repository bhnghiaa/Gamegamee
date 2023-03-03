package entities;

import gamestates.Playing;
import levels.Level;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

    private Playing playing;
    private BufferedImage[][] crabbyArr, pinkstarArr, sharkArr, minotaurArr, monsterArr;
    private Level currentLevel;

    public EnemyManager(Playing playing) {
        this.playing = playing;
        loadEnemyImg();
    }

    public void loadEnemies(Level level) {
        this.currentLevel = level;
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        for (Crabby c : currentLevel.getCrabs())
            if (c.isActive())
                if (c.getState() != DEAD && c.getState() != HIT)
                    if (attackBox.intersects(c.getHitbox())) {
                        c.hurt(15);
                        return; // can only hit 1 enemy per attack
                    }
        for (Minotaur m : currentLevel.getMinotaurs())
            if (m.isActive())
                if (m.getState() != DEAD && m.getState() != HIT)
                    if (attackBox.intersects(m.getHitbox())) {
                        m.hurt(15);
                        return; // can only hit 1 enemy per attack
                    }
        for (Pinkstar p : currentLevel.getPinkstars())
            if (p.isActive()) {
                if (p.getState() == ATTACK && p.getAniIndex() >= 3)
                    return;
                else {
                    if (p.getState() != DEAD && p.getState() != HIT)
                        if (attackBox.intersects(p.getHitbox())) {
                            p.hurt(15);
                            return;
                        }
                }
            }

        for (Shark s : currentLevel.getSharks())
            if (s.isActive()) {
                if (s.getState() != DEAD && s.getState() != HIT)
                    if (attackBox.intersects(s.getHitbox())) {
                        s.hurt(15);
                        return;
                    }
            }
        for (Monster mo : currentLevel.getMonsters())
            if (mo.isActive()) {
                if (mo.getState() != DEAD && mo.getState() != HIT)
                    if (attackBox.intersects(mo.getHitbox())) {
                        mo.hurt(15);
                        return;
                    }
            }
    }

    private void loadEnemyImg() {
        crabbyArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE), 9, 5, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
        pinkstarArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.PINKSTAR_ATLAS), 8, 5, PINKSTAR_WIDTH_DEFAULT, PINKSTAR_HEIGHT_DEFAULT);
        sharkArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.SHARK_ATLAS), 8, 5, SHARK_WIDTH_DEFAULT, SHARK_HEIGHT_DEFAULT);
        minotaurArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.MINOTAUR_ATLAS), 18, 5, MINOTAUR_WIDTH_DEFAULT, MINOTAUR_HEIGHT_DEFAULT);
        monsterArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.MONSTER_ATLAS), 10, 5, MONSTER_WIDTH_DEFAULT, MONSTER_HEIGHT_DEFAULT);
    }

    private BufferedImage[][] getImgArr(BufferedImage atlas, int xSize, int ySize, int spriteW, int spriteH) {
        BufferedImage[][] tempArr = new BufferedImage[ySize][xSize];
        for (int i = 0; i < tempArr.length; i++) {
            for (int j = 0; j < tempArr[i].length; j++) {
                tempArr[i][j] = atlas.getSubimage(j * spriteW, i * spriteH, spriteW, spriteH);
            }
        }
        return tempArr;
    }

    public void update(int[][] lvlData, Player player) {
        boolean isAnyActive = false;
        for (Crabby c : currentLevel.getCrabs()) {
            if (c.isActive()) {
                c.update(lvlData, player);
                isAnyActive = true;
            }
        }
        for (Minotaur m : currentLevel.getMinotaurs()) {
            if (m.isActive()) {
                m.update(lvlData, player);
                isAnyActive = true;
            }
        }
        for (Pinkstar p : currentLevel.getPinkstars()) {
            if (p.isActive()) {
                p.update(lvlData, player);
                isAnyActive = true;
            }
        }
        for (Shark s : currentLevel.getSharks()) {
            if (s.isActive()) {
                s.update(lvlData, player);
                isAnyActive = true;
            }
        }
        for (Monster mo : currentLevel.getMonsters()) {
            if (mo.isActive()) {
                mo.update(lvlData, player);
                isAnyActive = true;
            }
        }
        if (!isAnyActive) {
            playing.setLevelCompleted(true);
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawCrabs(g, xLvlOffset);
        drawPinkstars(g, xLvlOffset);
        drawSharks(g, xLvlOffset);
        drawMinotaurs(g, xLvlOffset);
        drawMonsters(g, xLvlOffset);
    }

    private void drawSharks(Graphics g, int xLvlOffset) {
        for (Shark s : currentLevel.getSharks())
            if (s.isActive()) {
                g.drawImage(sharkArr[s.getState()][s.getAniIndex()],
                        (int) s.getHitbox().x - xLvlOffset - SHARK_DRAWOFFSET_X + s.flipX(),
                        (int) s.getHitbox().y - SHARK_DRAWOFFSET_Y,
                        SHARK_WIDTH * s.flipW(), SHARK_HEIGHT, null);
//				s.drawHitbox(g, xLvlOffset);
//				s.drawAttackBox(g, xLvlOffset);
            }
    }

    private void drawPinkstars(Graphics g, int xLvlOffset) {
        for (Pinkstar p : currentLevel.getPinkstars())
            if (p.isActive()) {
                g.drawImage(pinkstarArr[p.getState()][p.getAniIndex()],
                        (int) p.getHitbox().x - xLvlOffset - PINKSTAR_DRAWOFFSET_X + p.flipX(),
                        (int) p.getHitbox().y - PINKSTAR_DRAWOFFSET_Y,
                        PINKSTAR_WIDTH * p.flipW(), PINKSTAR_HEIGHT, null);
//				p.drawHitbox(g, xLvlOffset);
            }
    }

    public void drawCrabs(Graphics g, int xLvlOffset) {
        for (Crabby c : currentLevel.getCrabs()) {
            if (c.isActive()) {
                g.drawImage(crabbyArr[c.getState()][c.getAniIndex()],
                        (int) c.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X + c.flipX(),
                        (int) c.getHitbox().y - CRABBY_DRAWOFFSET_Y,
                        CRABBY_WIDTH * c.flipW(), CRABBY_HEIGHT, null);

//                crabby.drawAttackBox(g, xLvlOffset);
            }
        }
    }

    public void drawMinotaurs(Graphics g, int xLvlOffset) {
        for (Minotaur m : currentLevel.getMinotaurs()) {
            if (m.isActive()) {
                g.drawImage(minotaurArr[m.getState()][m.getAniIndex()],
                        (int) m.getHitbox().x - xLvlOffset - MINOTAUR_DRAWOFFSET_X + m.miflipX(),
                        (int) (m.getHitbox().y - MINOTAUR_DRAWOFFSET_Y - 17 * Game.SCALE),
                        MINOTAUR_WIDTH * m.miflipW(), MINOTAUR_HEIGHT, null);

//                m.drawAttackBox(g, xLvlOffset);
            }
        }
    }

    public void drawMonsters(Graphics g, int xLvlOffset) {
        for (Monster mo : currentLevel.getMonsters()) {
            if (mo.isActive()) {
                g.drawImage(monsterArr[mo.getState()][mo.getAniIndex()],
                        (int) mo.getHitbox().x - xLvlOffset - MONSTER_DRAWOFFSET_X + mo.moflipX(),
                        (int) (mo.getHitbox().y - MONSTER_DRAWOFFSET_Y - MONSTER_HEIGHT + 54 * Game.SCALE),
                        MONSTER_WIDTH * mo.moflipW(), MONSTER_HEIGHT, null);
                mo.drawHitbox(g, xLvlOffset);
                mo.drawAttackBox(g, xLvlOffset);
            }
        }
    }

    public void resetAllEnemies() {
        for (Crabby c : currentLevel.getCrabs()) {
            c.resetEnemy();
        }
        for (Pinkstar p : currentLevel.getPinkstars()) {
            p.resetEnemy();
        }
        for (Shark s : currentLevel.getSharks()) {
            s.resetEnemy();
        }
        for (Minotaur m : currentLevel.getMinotaurs()) {
            m.resetEnemy();
        }
        for (Monster mo : currentLevel.getMonsters()) {
            mo.resetEnemy();
        }
    }
}
