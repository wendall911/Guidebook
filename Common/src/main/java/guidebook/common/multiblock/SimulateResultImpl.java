package guidebook.common.multiblock;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import guidebook.api.IMultiblock;
import guidebook.api.IStateMatcher;
import guidebook.common.util.RotationUtil;

public class SimulateResultImpl implements IMultiblock.SimulateResult {

	private final BlockPos worldPosition;
	private final IStateMatcher stateMatcher;
	@Nullable private final Character character;

	public SimulateResultImpl(BlockPos worldPosition, IStateMatcher stateMatcher, @Nullable Character character) {
		this.worldPosition = worldPosition;
		this.stateMatcher = stateMatcher;
		this.character = character;
	}

	@Override
	public BlockPos getWorldPosition() {
		return worldPosition;
	}

	@Override
	public IStateMatcher getStateMatcher() {
		return stateMatcher;
	}

	@Nullable
	@Override
	public Character getCharacter() {
		return character;
	}

	@Override
	public boolean test(Level world, Rotation rotation) {
		BlockState state = world.getBlockState(getWorldPosition()).rotate(RotationUtil.fixHorizontal(rotation));
		return getStateMatcher().getStatePredicate().test(world, getWorldPosition(), state);
	}

}
