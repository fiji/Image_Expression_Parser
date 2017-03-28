package fiji.expressionparser.function;

import java.util.Stack;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public abstract class SingleOperandPixelBasedAbstractFunction< T extends RealType< T > > extends PostfixMathCommand
		implements ImgLibFunction< T >
{

	@SuppressWarnings( "unchecked" )
	public final void run( final Stack inStack ) throws ParseException
	{
		checkStack( inStack ); // check the stack

		Object param = inStack.pop();
		Object result = null;

		if ( param instanceof Img< ? > )
		{

			result = evaluate( ( Img ) param );

		}
		else if ( param instanceof RealType )
		{

			FloatType t = ( FloatType ) param;
			result = new FloatType( evaluate( t ) ); // since this is
														// pixel-based, this
														// must be a singleton

		}
		else
		{
			throw new ParseException( "In function '" + getFunctionString()
					+ "': Bad type of operand: " + param.getClass().getSimpleName() );
		}

		inStack.push( result );
	}

	/**
	 * Return an ImgLib {@link Img} of {@link FloatType}, where every pixel is
	 * the function applied on the corresponding pixel of the source image.
	 * 
	 * @param img
	 *            The source image
	 * @return The resulting image
	 * @throws ParseException
	 */
	public final Img< FloatType > evaluate( final Img< T > img ) throws ParseException
	{
		// Create target image
		final long[] dimensions = new long[ img.numDimensions() ];
		img.dimensions( dimensions );
		Img< FloatType > result = new ArrayImgFactory< FloatType >()
				.create( dimensions, new FloatType() );

		Cursor< T > ic = img.cursor();
		Cursor< FloatType > rc = result.cursor();

		while ( rc.hasNext() )
		{
			rc.fwd();
			ic.fwd();
			rc.get().set( evaluate( ic.get() ) );
		}

		return result;

	}

	/**
	 * Evaluate this function a numeric types. Argument type can be of any
	 * numeric type, but calculation must be done on a float, so as to avoid
	 * underflow and overflow problems on bounded types (e.g. ByeType).
	 * 
	 * @param alpha
	 *            The first number as a {@link RealType}
	 * @return The resulting number as a float
	 * @throws ParseException
	 */
	public abstract < R extends RealType< R > > float evaluate( final R alpha ) throws ParseException;

}
