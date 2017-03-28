package fiji.expressionparser.function;

import java.util.Stack;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public abstract class TwoOperandsPixelBasedAbstractFunction< T extends RealType< T > > extends PostfixMathCommand implements ImgLibFunction< T >
{

	@SuppressWarnings( "unchecked" )
	@Override
	public final void run( final Stack inStack ) throws ParseException
	{
		checkStack( inStack ); // check the stack

		Object param2 = inStack.pop();
		Object param1 = inStack.pop();
		Object result = null;

		if ( param1 instanceof Img< ? > )
		{

			if ( param2 instanceof Img< ? > )
			{
				result = evaluate( ( Img ) param1, ( Img ) param2 );
			}
			else if ( param2 instanceof RealType )
			{
				FloatType t2 = ( FloatType ) param2;
				result = evaluate( ( Img ) param1, t2 );
			}
			else if ( param2 instanceof Double )
			{
				FloatType t2 = new FloatType( ( ( Double ) param2 ).floatValue() );
				result = evaluate( ( Img ) param1, t2 );
			}
			else
			{
				throw new ParseException( "In function '" + getFunctionString()
						+ "': Bad type of operand 2: " + param2.getClass().getSimpleName() );
			}

		}
		else if ( param1 instanceof RealType )
		{

			FloatType t1 = ( FloatType ) param1;

			if ( param2 instanceof Img< ? > )
			{
				result = evaluate( t1, ( Img ) param2 );
			}
			else if ( param2 instanceof RealType )
			{
				FloatType t2 = ( FloatType ) param2;
				result = new FloatType( evaluate( t1, t2 ) ); // since it is
																// pixel based,
																// this must be
																// a singleton
			}
			else
			{
				throw new ParseException( "In function '" + getFunctionString()
						+ "': Bad type of operand 2: " + param2.getClass().getSimpleName() );
			}

		}
		else
		{
			throw new ParseException( "In function '" + getFunctionString()
					+ "': Bad type of operand 1: " + param1.getClass().getSimpleName() );
		}

		inStack.push( result );
	}

	/**
	 * Evaluate this function on two images, and return result as an image.
	 * 
	 * @param img1
	 *            The first image
	 * @param img2
	 *            The second image
	 * @return The resulting image
	 */
	public final < R extends RealType< R > > Img< FloatType > evaluate( final Img< R > img1, final Img< R > img2 ) throws ParseException
	{

		// Create target image
		final long[] dimensions = new long[ img1.numDimensions() ];
		img1.dimensions( dimensions );
		Img< FloatType > result = new ArrayImgFactory< FloatType >()
				.create( dimensions, new FloatType() );

		// Check if all Containers are compatibles
		boolean compatible_containers = Util.equalIterationOrder( img1, img2 );

		if ( compatible_containers )
		{

			Cursor< R > c1 = img1.cursor();
			Cursor< R > c2 = img2.cursor();
			Cursor< FloatType > rc = result.cursor();
			while ( c1.hasNext() )
			{
				c1.fwd();
				c2.fwd();
				rc.fwd();
				rc.get().set( evaluate( c1.get(), c2.get() ) );
			}

		}
		else
		{

			Cursor< FloatType > rc = result.localizingCursor();
			RandomAccess< R > c1 = img1.randomAccess();
			RandomAccess< R > c2 = img2.randomAccess();
			while ( rc.hasNext() )
			{
				rc.fwd();
				c1.setPosition( rc );
				c2.setPosition( rc );
				rc.get().set( evaluate( c1.get(), c2.get() ) );
			}

		}

		return result;
	}

	/**
	 * Right-singleton expansion. Evaluate this function on an image and an
	 * image that would be of same dimension but with all element being the
	 * number passed in argument.
	 * 
	 * @param img
	 *            The image
	 * @param alpha
	 *            The number to do singleton expansion on
	 * @return The resulting image
	 */
	public final < R extends RealType< R > > Img< FloatType > evaluate( final Img< R > img, final R alpha ) throws ParseException
	{
		// Create target image
		final long[] dimensions = new long[ img.numDimensions() ];
		img.dimensions( dimensions );
		Img< FloatType > result = new ArrayImgFactory< FloatType >()
				.create( dimensions, new FloatType() );

		Cursor< R > ic = img.cursor();
		Cursor< FloatType > rc = result.cursor();

		while ( rc.hasNext() )
		{
			rc.fwd();
			ic.fwd();
			rc.get().set( evaluate( ic.get(), alpha ) );
		}

		return result;
	}

	/**
	 * Left-singleton expansion. Evaluate this function on an image and an image
	 * that would be of same dimension but with all element being the number
	 * passed in argument.
	 * 
	 * @param img
	 *            The image
	 * @param alpha
	 *            The number to do singleton expansion on
	 * @return The resulting image
	 */
	public final < R extends RealType< R > > Img< FloatType > evaluate( final R alpha, final Img< R > img ) throws ParseException
	{
		// Create target image
		final long[] dimensions = new long[ img.numDimensions() ];
		img.dimensions( dimensions );
		Img< FloatType > result = new ArrayImgFactory< FloatType >()
				.create( dimensions, new FloatType() );

		Cursor< R > ic = img.cursor();
		Cursor< FloatType > rc = result.cursor();

		while ( rc.hasNext() )
		{
			rc.fwd();
			ic.fwd();
			rc.get().set( evaluate( alpha, ic.get() ) );
		}

		return result;
	}

	/**
	 * Evaluate this function on two numeric types. Argument types can be of any
	 * numeric type, but a float must be returned, so as to avoid underflow and
	 * overflow problems on bounded types (e.g. ByeType).
	 * 
	 * @param t1
	 *            The first number
	 * @param t2
	 *            The second number
	 * @return The resulting number
	 */
	public abstract < R extends RealType< R > > float evaluate( final R t1, final R t2 ) throws ParseException;

}
