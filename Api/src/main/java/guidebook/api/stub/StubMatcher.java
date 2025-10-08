package guidebook.api.stub;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import guidebook.api.IStateMatcher;
import guidebook.api.TriPredicate;

public final class StubMatcher implements IStateMatcher {

	public static final StubMatcher INSTANCE = new StubMatcher();

	private final BlockState state = Blocks.AIR.defaultBlockState();

	private StubMatcher() {}

	@Override
	public BlockState getDisplayedState(long ticks) {
		return state;
	}

	@Override
	public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
		return (w, p, s) -> false;
	}

}
