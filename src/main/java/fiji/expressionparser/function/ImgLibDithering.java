package fiji.expressionparser.function;

import java.util.Stack;

import net.imglib2.algorithm.floydsteinberg.FloydSteinbergDithering;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import fiji.expressionparser.ImgLibUtils;

public class ImgLibDithering <T extends RealType<T>> extends PostfixMathCommand implements ImgLibFunction<T> {


	public ImgLibDithering() {
		numberOfParameters = -1;
	}
	
	@Override
	public boolean checkNumberOfParameters(int n) {
		return (n == 1 || n == 2);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void run(Stack stack) throws ParseException {
		
		// Check that we are not empty
		checkStack(stack);
		
		
		// Deal with 1 or 2 parameters
		FloydSteinbergDithering<T> dither = null;
		Img<T> img;
		if (curNumberOfParameters == 1) {
			Object param = stack.pop();
			
			if (param instanceof Img<?>) {
				img = (Img) param;
			} else {
				throw new ParseException("In function "+getFunctionString()
						+": First argument must be an image, got a "+param.getClass().getSimpleName());
			}
			
			dither = new FloydSteinbergDithering<T>(img);
			
		} else {
			// Two parameters, 2nd one is threshold
			Object param2 = stack.pop();
			Object param1 = stack.pop();

			if (param1 instanceof Img<?>) {
				img = (Img) param1;
			} else {
				throw new ParseException("In function "+getFunctionString()
						+": First argument must be an image, got a "+param1.getClass().getSimpleName());
			}
			
			if (param2 instanceof RealType) {
				float dither_threshold = ( (FloatType)param2).getRealFloat(); 
				dither = new FloydSteinbergDithering<T>(img, dither_threshold);
			} else {
				throw new ParseException("In function "+getFunctionString()
						+": Second argument must be a number, got a "+param2.getClass().getSimpleName());
			}
		}

		// Process
		dither.process();
		Img<BitType> result = dither.getResult();
		if (result == null) {
			throw new RuntimeException("Floyd-Steinberg dithering unfortunately not available with this version of ImgLib2!");
		}
		Img<FloatType> float_result = ImgLibUtils.copyToFloatTypeImage(result); // we return result as a float image 
		stack.push(float_result);		
	}

	@Override
	public String getDocumentationString() {
		return "<h3>Floyd-Steinberg dithering</h3> " +
		"This function returns a 2 level, dithered version of the input, using " +
		"the Floyd-Steinberg algorithm (possibily extended to nD). " +
		"Syntax: " +
		"<br><code>" + getFunctionString() + "(A)</code><br> " +
		"or <br><code>" + getFunctionString() + "(A, thr)</code><br> " +
		"with A an image and thr the threshold used for dithering. ";				
	}

	@Override
	public String getFunctionString() {
		return "dither";
	}
	
}
