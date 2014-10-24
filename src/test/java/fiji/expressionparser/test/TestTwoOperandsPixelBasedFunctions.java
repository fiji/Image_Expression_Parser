/**
 * 
 */
package fiji.expressionparser.test;


import static fiji.expressionparser.test.TestUtilities.doTest;
import static fiji.expressionparser.test.TestUtilities.doTestNumbers;
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


/**
 * @author Jean-Yves Tinevez
 *
 */
public class TestTwoOperandsPixelBasedFunctions {
	
	private Map<String, Img<UnsignedShortType>> source_map; 
	{
		source_map = new HashMap<String, Img<UnsignedShortType>>();
		source_map.put("A", image_A);
	}
	
	
	@Test
	public void atan2TwoImage() throws ParseException {
		String expression = "atan2(A,A)";;	
		ExpectedExpression ee = new ExpectedExpression() {
			@Override
			public final <T extends RealType<T>> float getExpectedValue(Map<String, RandomAccess<T>> cursors) {
				final RandomAccess<T> source = cursors.get("A");
				return source.get().getRealFloat() == 0f? 0f : (float) (45*Math.PI/180);
			}
		};
		doTest(expression, source_map, ee);
	}
	
	@Test
	public void atan2RightSingletonExpansion() throws ParseException {
		String expression = "atan2(A,10)";
		ExpectedExpression ee = new ExpectedExpression() {
			@Override
			public final <T extends RealType<T>>  float getExpectedValue(Map<String, RandomAccess<T>> cursors) {
				final RandomAccess<T> source = cursors.get("A");
				return (float) Math.atan(source.get().getRealFloat() / 10 );
			}
		};
		doTest(expression, source_map, ee);
	}

	@Test
	public void atan2LeftSingletonExpansion() throws ParseException {
		String expression = "atan2(100, A)";
		ExpectedExpression ee = new ExpectedExpression() {
			@Override
			public final <T extends RealType<T>>  float getExpectedValue(Map<String, RandomAccess<T>> cursors) {
				final RandomAccess<T> source = cursors.get("A");
				return (float) Math.atan(100 / source.get().getRealFloat() );
			}
		};
		doTest(expression, source_map, ee);
	}
	
	@Test
	public void atan2TwoNumbers() throws ParseException {
		String expression = "atan2(1.14, 1.14)";
		doTestNumbers(expression, (float) (45*Math.PI/180));
	}
		
	
	
}
