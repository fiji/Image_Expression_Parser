package fiji.expressionparser.test;

import static fiji.expressionparser.test.TestUtilities.doTest;
import static fiji.expressionparser.test.TestUtilities.image_A;

import java.util.HashMap;
import java.util.Map;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.junit.Test;
import org.nfunk.jep.ParseException;

import fiji.expressionparser.test.TestUtilities.ExpectedExpression;

public class TestSingleOperandPixelBasedOperators  <T extends RealType<T>> {

	private Map<String, Img<UnsignedShortType>> source_map; 
	{
		source_map = new HashMap<String, Img<UnsignedShortType>>();
		source_map.put("A", image_A);
	}


	@Test
	public void uMinus() throws ParseException {
		String expression = "-A" ;
		doTest(expression, source_map, new ExpectedExpression() {
			@Override
			public <R extends RealType<R>> float getExpectedValue(final Map<String, RandomAccess<R>> cursors) {
				final RandomAccess<R> cursor = cursors.get("A");
				return -cursor.get().getRealFloat();
			}
		});
	}	

	@Test
	public void not() throws ParseException {
		String expression = "!A" ;
		doTest(expression, source_map, new ExpectedExpression() {
			@Override
			public <R extends RealType<R>> float getExpectedValue(final Map<String, RandomAccess<R>> cursors) {
				RandomAccess<R> cursor = cursors.get("A");
				return cursor.get().getRealFloat() == 0f ? 1.0f : 0.0f;
			}
		});
	}	

}
