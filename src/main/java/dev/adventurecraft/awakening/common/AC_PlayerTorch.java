package dev.adventurecraft.awakening.common;

import dev.adventurecraft.awakening.extension.client.ExMinecraft;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class AC_PlayerTorch {

    static boolean torchActive;
    static float moveThreshold = 0.05F;
    static float posX;
    static float posY;
    static float posZ;
    static int iX;
    static int iY;
    static int iZ;
    static int torchBrightness = 15;
    static int range = torchBrightness * 2 + 1;
    static float[] cache = new float[range * range * range];

    public static boolean isTorchActive() {
        return torchActive;
    }

    public static void setTorchState(World world, boolean active) {
        if (torchActive != active) {
            torchActive = active;
            markBlocksDirty(world);
        }
    }

    public static void setTorchPos(World world, float x, float y, float z) {
        long avgFrameTime = ((ExMinecraft) Minecraft.instance).getAvgFrameTime();
        int updateRate = 1;
        if (avgFrameTime > 33333333L) {
            updateRate = 3;
        } else if (avgFrameTime > 16666666L) {
            updateRate = 2;
        }

        float dX = Math.abs(x - posX);
        float dY = Math.abs(y - posY);
        float dZ = Math.abs(z - posZ);
        if ((dX > moveThreshold || dY > moveThreshold || dZ > moveThreshold) &&
            (int) world.getWorldTime() % updateRate == 0L) {
            posX = x;
            posY = y;
            posZ = z;
            iX = (int) posX;
            iY = (int) posY;
            iZ = (int) posZ;
            markBlocksDirty(world);
        }
    }

    public static float getTorchLight(World world, int x, int y, int z) {
        if (torchActive) {
            int bX = x - iX + torchBrightness;
            int bY = y - iY + torchBrightness;
            int bZ = z - iZ + torchBrightness;
            if (bX >= 0 && bX < range && bY >= 0 && bY < range && bZ >= 0 && bZ < range) {
                return cache[bX * range * range + bY * range + bZ];
            }
        }

        return 0.0F;
    }

    private static void markBlocksDirty(World world) {
        float baseX = posX - (float) iX;
        float baseY = posY - (float) iY;
        float baseZ = posZ - (float) iZ;
        int index = 0;

        for (int rX = -torchBrightness; rX <= torchBrightness; ++rX) {
            int x = rX + iX;

            for (int rY = -torchBrightness; rY <= torchBrightness; ++rY) {
                int y = rY + iY;

                for (int rZ = -torchBrightness; rZ <= torchBrightness; ++rZ) {
                    int z = rZ + iZ;

                    int id = world.getBlockId(x, y, z);
                    float result = 0.0F;
                    if (id == 0 || !Block.BY_ID[id].isFullOpaque() || id == Block.STONE_SLAB.id || id == Block.FARMLAND.id) {
                        float brightness = (float) (Math.abs((double) rX + 0.5D - (double) baseX) + Math.abs((double) rY + 0.5D - (double) baseY) + Math.abs((double) rZ + 0.5D - (double) baseZ));
                        if (brightness <= (float) torchBrightness) {
                            if ((float) torchBrightness - brightness > (float) world.placeBlock(x, y, z)) {
                                world.notifyListeners(x, y, z);
                            }

                            result = (float) torchBrightness - brightness;
                        }
                    }
                    cache[index++] = result;
                }
            }
        }
    }
}
