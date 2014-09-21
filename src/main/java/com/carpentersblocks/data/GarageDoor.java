package com.carpentersblocks.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.registry.BlockRegistry;

public class GarageDoor implements ISided {

    /**
     * 16-bit data components:
     *
     * [00000000] [0]   [000] [0000]
     * Unused     State Dir   Type
     */

    public static final int TYPE_DEFAULT   = 0;
    public static final int TYPE_GLASS_TOP = 1;
    public static final int TYPE_GLASS     = 2;
    public static final int TYPE_SIDING    = 3;

    public final static int STATE_CLOSED = 0;
    public final static int STATE_OPEN   = 1;

    /**
     * Returns type.
     */
    public int getType(TEBase TE)
    {
        return BlockProperties.getMetadata(TE) & 0xf;
    }

    /**
     * Sets type.
     */
    public void setType(TEBase TE, int type)
    {
        int temp = (BlockProperties.getMetadata(TE) & ~0xf) | type;
        BlockProperties.setMetadata(TE, temp);
    }

    /**
     * Returns direction.
     */
    @Override
    public ForgeDirection getDirection(TEBase TE)
    {
        int side = (BlockProperties.getMetadata(TE) & 0x70) >> 4;
        return ForgeDirection.getOrientation(side);
    }

    /**
     * Sets direction.
     */
    @Override
    public void setDirection(TEBase TE, ForgeDirection dir)
    {
        int temp = (BlockProperties.getMetadata(TE) & ~0x70) | dir.ordinal() << 4;
        BlockProperties.setMetadata(TE, temp);
    }

    /**
     * Returns state (open or closed).
     */
    public int getState(TEBase TE)
    {
        return (BlockProperties.getMetadata(TE) & 0x80) >> 7;
    }

    /**
     * Sets state (open or closed).
     */
    public void setState(TEBase TE, int state, boolean playSound)
    {
        int temp = (BlockProperties.getMetadata(TE) & ~0x80) | state << 7;

        World world = TE.getWorldObj();
        if (!world.isRemote && playSound) {
            world.playAuxSFXAtEntity((EntityPlayer)null, 1003, TE.xCoord, TE.yCoord, TE.zCoord, 0);
        }

        BlockProperties.setMetadata(TE, temp);
    }

    public boolean isOpen(TEBase TE)
    {
        return getState(TE) == STATE_OPEN;
    }

    /**
     * Weather panel is the topmost.
     *
     * @param  TE the {@link TEBase}
     * @return true if panel is the topmost
     */
    public boolean isTopmost(TEBase TE)
    {
        return !TE.getWorldObj().getBlock(TE.xCoord, TE.yCoord + 1, TE.zCoord).equals(BlockRegistry.blockCarpentersGarageDoor);
    }

    /**
     * Gets the topmost garage door tile entity.
     *
     * @param  TE the {@link TEBase}
     * @return the {@link TEBase}
     */
    public TEBase getTopmost(World world, int x, int y, int z)
    {
        do {
            ++y;
        } while (world.getBlock(x, y, z).equals(BlockRegistry.blockCarpentersGarageDoor));

        return (TEBase) world.getTileEntity(x, y - 1, z);
    }

    /**
     * Gets the bottommost garage door tile entity.
     *
     * @param  TE the {@link TEBase}
     * @return the {@link TEBase}
     */
    public TEBase getBottommost(World world, int x, int y, int z)
    {
        do {
            --y;
        } while (world.getBlock(x, y, z).equals(BlockRegistry.blockCarpentersGarageDoor));

        return (TEBase) world.getTileEntity(x, y + 1, z);
    }

    /**
     * Whether block is visible.
     * <p>
     * If a block is open and not in the topmost position,
     * it cannot be selected or collided with.
     *
     * @param  TE the {@link TEBase}
     * @return true if visible
     */
    public boolean isVisible(TEBase TE)
    {
        if (isOpen(TE)) {
            return isTopmost(TE);
        } else {
            return true;
        }
    }

}
