package fiji.expressionparser.test;

import static org.junit.Assert.assertEquals;
import fiji.expressionparser.ImgLibOperatorSet;
import fiji.expressionparser.ImgLibParser;

import java.io.PrintStream;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 * Test cases for parser functions that are part of the {@link ImgLibOperatorSet}. As
 * such, they have an operator-like syntax, such as A + B. 
 * @author Jean-Yves Tinevez
 */
public class TestTwoOperandsPixelBasedOperators <T extends RealType<T>>{
	
	
	private final static int WIDTH = 9; 
	private final static int HEIGHT = 9;
	/** 16-bit image */
	public static Img<UnsignedShortType> image_A, image_B;
	public ImgLibParser<T> parser;
	
	
	@BeforeClass
	public static void setup() {
		
		
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
			cb.get().set( (int)(256 - (pos[0] * (WIDTH-1-pos[0]) * pos[1] * (HEIGHT-1-pos[1])) ) );
			
		}
//		echoImage(image_A, System.out);
//		echoImage(image_B, System.out);		
	}
	
	@Before
	public void setupParser() {
		parser = new ImgLibParser<T>(); // no need to add standard functions
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void add() throws ParseException {
		
		// Addition of two images
		String expression = "A+B";
		parser.addVariable("A", image_A);
		parser.addVariable("B", image_B);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.cursor();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				assertEquals(256.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Singleton expansion
		expression = "A+256";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		RandomAccess<T> rclbd = result.randomAccess();
		Cursor<UnsignedShortType> ca = image_A.localizingCursor();
		try {
			while (ca.hasNext()) {
				ca.fwd();
				rclbd.setPosition(ca);
				assertEquals(256.0f+ca.get().getRealFloat(), rclbd.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers addition
		expression = "256+256";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(512.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void subtract() throws ParseException {
		
		// Two images
		String expression = "A-A";
		parser.addVariable("A", image_A);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.cursor();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				assertEquals(0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A-256";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		RandomAccess<T> rclbd = result.randomAccess();
		Cursor<UnsignedShortType> ca = image_A.localizingCursor();
		try {
			while (ca.hasNext()) {
				ca.fwd();
				rclbd.setPosition(ca);
				assertEquals(ca.get().getRealFloat()-256.0f, rclbd.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Left-singleton expansion
		expression = "256-A";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rclbd = result.randomAccess();
		ca = image_A.localizingCursor();
		try {
			while (ca.hasNext()) {
				ca.fwd();
				rclbd.setPosition(ca);
				assertEquals(256.0f-ca.get().getRealFloat(), rclbd.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "256-128";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(128.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void multiply() throws ParseException {
		
		// Two images
		String expression = "A*B";
		parser.addVariable("A", image_A);
		parser.addVariable("B", image_B);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		RandomAccess<UnsignedShortType> cb = image_B.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				cb.setPosition(rc);
				assertEquals(ca.get().getRealFloat() * cb.get().getRealFloat(), 
						rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A*10";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()*10.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "10*A";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()*10.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "256*10";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(2560.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void divide() throws ParseException {
		
		// Two images - we also check for division by 0
		String expression = "A/A";
		parser.addVariable("A", image_A);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				if ( ca.get().getRealFloat() == 0.0f) {
					assertEquals(Float.NaN, rc.get().getRealFloat(), Float.MIN_VALUE);
				} else {
					assertEquals(1.0f,	rc.get().getRealFloat(), Float.MIN_VALUE);
				}
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A/10";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()/10.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "10/A";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(10/ca.get().getRealFloat(), rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "256/10";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(25.6f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	

	
	@SuppressWarnings("unchecked")
	@Test
	public void lowerThan() throws ParseException {
		
		// Two images - we also check for division by 0
		String expression = "A < A";
		parser.addVariable("A", image_A);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(0.0f, rc.get().getRealFloat(), Float.MIN_VALUE); // never true
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A < 128";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()<128? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "128 < A";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()>128? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "128 < 128.0001";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(1.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void greaterThan() throws ParseException {
		
		// Two images
		String expression = "A > A";
		parser.addVariable("A", image_A);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(0.0f, rc.get().getRealFloat(), Float.MIN_VALUE); // never true
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A > 128";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()>128? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "128 > A";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()<128? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "128 > 128.0001";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(0.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void greaterOrEqual() throws ParseException {
		
		// Two images
		String expression = "A >= A";
		parser.addVariable("A", image_A);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(1.0f, rc.get().getRealFloat(), Float.MIN_VALUE); // always true
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A >= 128";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()>=128? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "128 >= A";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()<=128? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "128 >= 128.0001";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(0.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void lowerOrEqual() throws ParseException {
		
		// Two images
		String expression = "A <= A";
		parser.addVariable("A", image_A);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(1.0f, rc.get().getRealFloat(), Float.MIN_VALUE); // always true
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A <= 128";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()<=128? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "128 <= A";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()>=128? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "128 <= 128.0001";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(1.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	


	@SuppressWarnings("unchecked")
	@Test
	public void equal() throws ParseException {
		
		// Two images
		String expression = "A == A";
		parser.addVariable("A", image_A);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(1.0f, rc.get().getRealFloat(), Float.MIN_VALUE); // always true
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A == 128";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()==128f? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "128 == A";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()==128f? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "128 == 128.0001";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(0.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	


	@SuppressWarnings("unchecked")
	@Test
	public void notEqual() throws ParseException {
		
		// Two images
		String expression = "A != A";
		parser.addVariable("A", image_A);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(0.0f, rc.get().getRealFloat(), Float.MIN_VALUE); // never true
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A != 128";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()==128f? 0.0f:1.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "128 != A";
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()==128f? 0.0f:1.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "128 != 128.0001";
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(1.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	


	@SuppressWarnings("unchecked")
	@Test
	public void and() throws ParseException {
		
		// Two images
		String expression = "A && B";
		parser.addVariable("A", image_A);
		parser.addVariable("B", image_B);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		RandomAccess<UnsignedShortType> cb = image_B.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				cb.setPosition(rc);
				assertEquals( 
						(ca.get().getRealFloat() != 0f && cb.get().getRealFloat() != 0f)? 1.0f:0.0f,
						 rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A && 1"; // will be true if A is non zero
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()!=0f? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "0 && A"; // always false
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "128 && 1"; // true
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(1.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
	


	@SuppressWarnings("unchecked")
	@Test
	public void or() throws ParseException {
		
		// Two images
		String expression = "A || B"; // always true in this case
		parser.addVariable("A", image_A);
		parser.addVariable("B", image_B);
		Node root_node = parser.parse(expression);
		Img<T> result = (Img<T>) parser.evaluate(root_node);
		Cursor<T> rc = result.localizingCursor();
		RandomAccess<UnsignedShortType> ca = image_A.randomAccess(); 
		RandomAccess<UnsignedShortType> cb = image_B.randomAccess(); 
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				cb.setPosition(rc);
				assertEquals(1.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}

		// Right-singleton expansion
		expression = "A || 0"; // will be true if A is non zero
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(ca.get().getRealFloat()!=0f? 1.0f:0.0f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
			
		// Left-singleton expansion
		expression = "1 || A"; // always true
		root_node = parser.parse(expression);
		result = (Img<T>) parser.evaluate(root_node);
		rc = result.localizingCursor();
		ca = image_A.randomAccess();
		try {
			while (rc.hasNext()) {
				rc.fwd();
				ca.setPosition(rc);
				assertEquals(1f, rc.get().getRealFloat(), Float.MIN_VALUE);
			}
		} catch (AssertionError ae) {
			System.out.println("Assertion failed on "+expression+" with result:");
			echoImage(result, System.out);
			throw (ae);
		} finally {
		}
		
		// Numbers 
		expression = "0 || 1"; // true
		root_node = parser.parse(expression);
		FloatType number_result = (FloatType) parser.evaluate(root_node);
		assertEquals(1.0f, number_result.getRealFloat(), Float.MIN_VALUE);
	}
		
	


		
	
	
	/*
	 * UTILS
	 */
	
	public static final <T extends RealType<T>> void echoImage(Img<T> img, PrintStream logger) {
		RandomAccess<T> lc = img.randomAccess();
		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);
		
		logger.append(img.toString() + "\n");
		logger.append("        ");
		for (int i =0; i<dims[0]; i++) {
			logger.append(String.format("%6d.", i) );				
		}
		logger.append('\n');
		logger.append('\n');
		
		for (int j = 0; j<dims[1]; j++) {
			
			lc.setPosition(j, 1);
			logger.append(String.format("%2d.  -  ", j) );				
			for (int i =0; i<dims[0]; i++) {
				lc.setPosition(i, 0);
				logger.append(String.format("%7.1f", lc.get().getRealFloat()));				
			}
			logger.append('\n');
		}
		
	}

}
