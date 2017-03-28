package fiji.expressionparser.test;

import static fiji.expressionparser.test.TestUtilities.doTest;
import static fiji.expressionparser.test.TestUtilities.image_A;
import fiji.expressionparser.test.TestUtilities.ExpectedExpression;

import java.util.HashMap;
import java.util.Map;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.junit.Test;
import org.nfunk.jep.ParseException;

public class TestSingleOperandPixelBasedFunctions< T extends RealType< T > >
{

	private Map< String, Img< UnsignedShortType > > source_map;
	{
		source_map = new HashMap< String, Img< UnsignedShortType > >();
		source_map.put( "A", image_A );
	}

	@Test
	public void abs() throws ParseException
	{
		String expression = "abs(A)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return Math.abs( cursor.get().getRealFloat() );
			}
		} );
	}

	@Test
	public void arcCosine() throws ParseException
	{
		String expression = "acos(A)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.acos( cursor.get().getRealFloat() );
			}
		} );
	}

	@Test
	public void arcSine() throws ParseException
	{
		String expression = "asin(A)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.asin( cursor.get().getRealFloat() );
			}
		} );
	}

	@Test
	public void arcTangent() throws ParseException
	{
		String expression = "atan(A)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.atan( cursor.get().getRealFloat() );
			}
		} );
	}

	@Test
	public void ceil() throws ParseException
	{
		String expression = "ceil(A/3)"; // to generate non even numbers
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.ceil( cursor.get().getRealFloat() / 3 );
			}
		} );
	}

	@Test
	public void cosine() throws ParseException
	{
		String expression = "cos(A)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.cos( cursor.get().getRealFloat() );
			}
		} );
	}

	@Test
	public void exponential() throws ParseException
	{
		String expression = "exp(A)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.exp( cursor.get().getRealFloat() );
			}
		} );
	}

	@Test
	public void floor() throws ParseException
	{
		String expression = "floor(A/3)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.floor( cursor.get().getRealFloat() / 3 );
			}
		} );
	}

	@Test
	public void logarithm() throws ParseException
	{
		String expression = "log(A)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.log( cursor.get().getRealFloat() );
			}
		} );
	}

	@Test
	public void round() throws ParseException
	{
		String expression = "round(A/3)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.round( cursor.get().getRealFloat() / 3 );
			}
		} );
	}

	@Test
	public void sine() throws ParseException
	{
		String expression = "sin(A)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.sin( cursor.get().getRealFloat() );
			}
		} );
	}

	@Test
	public void sqrt() throws ParseException
	{
		String expression = "sqrt(A-100)"; // we want NaNs
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.sqrt( cursor.get().getRealFloat() - 100 );
			}
		} );
	}

	@Test
	public void tangent() throws ParseException
	{
		String expression = "tan(A)";
		doTest( expression, source_map, new ExpectedExpression()
		{
			@Override
			public < R extends RealType< R > > float getExpectedValue( final Map< String, RandomAccess< R > > cursors )
			{
				final RandomAccess< R > cursor = cursors.get( "A" );
				return ( float ) Math.tan( cursor.get().getRealFloat() );
			}
		} );
	}

}
