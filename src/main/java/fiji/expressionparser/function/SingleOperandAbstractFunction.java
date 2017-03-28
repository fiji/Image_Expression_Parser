package fiji.expressionparser.function;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public abstract class SingleOperandAbstractFunction< T extends RealType< T > > extends PostfixMathCommand implements ImgLibFunction< T >
{

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public final void run( final Stack inStack ) throws ParseException
	{
		checkStack( inStack ); // check the stack

		final Object param = inStack.pop();
		Object result = null;

		if ( param instanceof Img< ? > )
		{

			final Img< T > img = ( Img ) param;
			result = evaluate( img );

		}
		else if ( param instanceof FloatType )
		{

			final FloatType t = ( FloatType ) param;
			result = evaluate( t );

		}
		else
		{
			throw new ParseException( "In function '" + getFunctionString()
					+ "': Bad type of operand: " + param.getClass().getSimpleName() );
		}

		inStack.push( result );
	}

	/**
	 * Evaluate this function on one ImgLib images. A new {@link Img} of
	 * {@link FloatType} is returned, so as to avoid underflow and overflow
	 * problems on bounded types (e.g. ByeType).
	 * 
	 * @param img
	 *            the image
	 * @return The new resulting image
	 */
	public abstract < R extends RealType< R > > Img< FloatType > evaluate( final Img< R > img ) throws ParseException;

	/**
	 * Evaluate this function on a numeric {@link RealType} type. A new
	 * {@link Img} of {@link FloatType} is returned, so as to avoid underflow
	 * and overflow problems on bounded types (e.g. ByeType).
	 * 
	 * @param alpha
	 *            the numeric type
	 * @return The new resulting image
	 */
	public abstract < R extends RealType< R > > Img< FloatType > evaluate( final R alpha ) throws ParseException;

}
