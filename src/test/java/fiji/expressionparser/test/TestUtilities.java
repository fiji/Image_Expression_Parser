package fiji.expressionparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import fiji.expressionparser.ImgLibParser;
import fiji.expressionparser.ImgLibUtils;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class TestUtilities <T extends RealType<T>> {
	
	private final static int WIDTH = 9; 
	private final static int HEIGHT = 9;
	private final static float ERROR_TOLERANCE = 1e-6f;
	/** 16-bit image */
	public static Img<UnsignedShortType> image_A, image_B; 
	static {	
		// Create source images
		UnsignedShortType type = new UnsignedShortType();
		ImgFactory<UnsignedShortType> ifact = new ArrayImgFactory<UnsignedShortType>();
		
		image_A = ifact.create(new int[] {WIDTH, HEIGHT}, type);
		image_B = ifact.create(new int[] {WIDTH, HEIGHT}, type);
		
		Cursor<UnsignedShortType> ca = image_A.localizingCursor();
		RandomAccess<UnsignedShortType> cb = image_B.randomAccess();

		long[] pos = new long[ca.numDimensions()];
		while (ca.hasNext()) {
			ca.fwd();
			ca.localize(pos);
			cb.setPosition(ca);
			ca.get().set( (int)(pos[0] * (WIDTH-1-pos[0]) * pos[1] * (HEIGHT-1-pos[1])) );
			cb.get().set( (int)(256 - (pos[0] * (WIDTH-1-pos[0]) * pos[1] * (HEIGHT-1-pos[1]) )) );
		}
	}
	
	
	public static interface ExpectedExpression {
		public <T extends RealType<T>> float getExpectedValue(final Map<String, RandomAccess<T>> cursors);
	}
	
	
	public static final <T extends RealType<T>> void doTest(String expression, Map<String, Img<T>> source_map, ExpectedExpression ee) throws ParseException {
		// Get result
		Img<FloatType> result = getEvaluationResult(expression, source_map);
		// Prepare expected image		
		Img<FloatType> expected = buildExpectedImage(source_map, ee);		
		// Compare
		Img<FloatType> error = buildErrorImage(expected, result);
		boolean passed = checkErrorImage(error);
		try {
			assertTrue(passed);		
		} catch (AssertionError ae) {
			System.out.println("\n---");
			System.out.println("Assertion failed on "+expression+" with error image:");
			echoImage(error, System.out);
			throw (ae);
		}
	}
	
	public static final  void doTestNumbers(String expression, float expected) throws ParseException {
		ImgLibParser<FloatType> parser = new ImgLibParser<FloatType>();
		parser.addStandardFunctions();
		parser.addImgLibAlgorithms();
		Node root_node = parser.parse(expression);
		FloatType result = (FloatType) parser.evaluate(root_node);
		assertEquals(result.get(), expected, ERROR_TOLERANCE);
	}
		
	@SuppressWarnings("unchecked")
	public static final <T extends RealType<T>> Img<FloatType> getEvaluationResult(final String expression, final Map<String, Img<T>> source_map) throws ParseException {
		ImgLibParser<T>  parser = new ImgLibParser<T>();
		parser.addStandardFunctions();
		parser.addImgLibAlgorithms();
		for (String key : source_map.keySet()) {
			parser.addVariable(key, source_map.get(key));
		}
		Node root_node = parser.parse(expression);
		Img<FloatType> result = (Img<FloatType>) parser.evaluate(root_node);
		return result;
	}
	
	public static final <T extends RealType<T>> Img<FloatType> buildExpectedImage(final Map<String, Img<T>> source_map, final ExpectedExpression expression) {
		Img<T> source = source_map.get(source_map.keySet().toArray()[0]);
		long[] dimensions = new long[source.numDimensions()];
		source.dimensions(dimensions);
		Img<FloatType> expected = new ArrayImgFactory<FloatType>()
			.create(dimensions, new FloatType());
		// Prepare cursors
		Cursor<FloatType> ec = expected.localizingCursor();
		Map<String, RandomAccess<T>> cursor_map = new HashMap<String, RandomAccess<T>>(source_map.size());
		for ( String key : source_map.keySet()) {
			cursor_map.put(key, source_map.get(key).randomAccess());
		}
		// Set target value by looping over pixels
		while (ec.hasNext()) {
			ec.fwd();
			for (String key : cursor_map.keySet()) {
				cursor_map.get(key).setPosition(ec);
			}
			ec.get().set(expression.getExpectedValue(cursor_map));
		}		
		// Return
		return expected;		
	}
		
	public static final Img<FloatType> buildErrorImage(Img<FloatType> expected, Img<FloatType> actual) {
		Img<FloatType> result = ImgLibUtils.copyToFloatTypeImage(expected);
		Cursor<FloatType> rc = result.localizingCursor();
		RandomAccess<FloatType> ec = expected.randomAccess();
		RandomAccess<FloatType> ac = actual.randomAccess();
		while (rc.hasNext()) {
			rc.fwd();
			ec.setPosition(rc);
			ac.setPosition(rc);
			rc.get().set(Math.abs(ec.get().get()- ac.get().get()));
		}
		return result;
	}
	
	public static final boolean checkErrorImage(Img<FloatType> error) {
		boolean ok = true;
		Cursor<FloatType> c = error.cursor();
		while (c.hasNext()) {
			c.fwd();
			if (c.get().get() > ERROR_TOLERANCE) {
				ok = false;
				break;
			}
		}
		return ok;
	}
	
	
	public static final <T extends RealType<T>> void echoImage(Img<T> img, PrintStream logger) {
		RandomAccess<T> lc = img.randomAccess();
		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);

		logger.append(img.toString() + "\n");
		logger.append("        ");
		for (int i =0; i<dims[0]; i++) {
			logger.append(String.format("%9d.", i) );				
		}
		logger.append('\n');

		for (int j = 0; j<dims[1]; j++) {

			lc.setPosition(j, 1);
			logger.append(String.format("%2d.  -  ", j) );				
			for (int i =0; i<dims[0]; i++) {
				lc.setPosition(i, 0);
				logger.append(String.format("%10.1e", lc.get().getRealFloat()));				
			}
			logger.append('\n');
		}

	}
	

}
