package mirrg.beryllium.event;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

public class TestEventManager
{

	@Test
	public void test()
	{
		String[] message = {
			"",
		};

		IEventProvider<Integer> eventManager = IEventProvider.createInstance();
		eventManager.register(Integer.class, event -> {
			message[0] += event;
		});

		eventManager.fire(Integer.valueOf(4));
		assertEquals("4", message[0]);
	}

	@Test
	public void test2()
	{
		String[] message = {
			"",
		};

		IEventProvider<Object> eventManager = IEventProvider.createInstance();
		eventManager.register(Integer.class, event -> {
			message[0] += event;
		});
		eventManager.register(String.class, event -> {
			message[0] += event;
		});

		eventManager.fire(Integer.valueOf(4));
		assertEquals("4", message[0]);

		eventManager.fire("G");
		assertEquals("4G", message[0]);

	}

	@Test
	public void test3()
	{
		String[] message = {
			"",
		};

		IEventProvider<Object> eventManager = IEventProvider.createInstance();
		eventManager.register(Integer.class, event -> {
			message[0] += "I";
		});
		eventManager.register(String.class, event -> {
			message[0] += "S";
		});
		eventManager.register(Double.class, event -> {
			message[0] += "D";
		});
		eventManager.register(Number.class, event -> {
			message[0] += "N";
		});

		eventManager.fire(Integer.valueOf(4));
		assertEquals("IN", message[0]);

		eventManager.fire("G");
		assertEquals("INS", message[0]);

		eventManager.fire((Object) Integer.valueOf(4));
		assertEquals("INSIN", message[0]);

		eventManager.fire(Double.valueOf(4));
		assertEquals("INSINDN", message[0]);

	}

	@Test
	public void test4()
	{
		String[] message = {
			"",
		};

		IEventProvider<Object> eventManager = IEventProvider.createInstance();

		eventManager.fire(new Object());
		assertEquals("", message[0]);

		eventManager.register(Object.class, event -> {
			message[0] += "A";
		});

		eventManager.fire(new Object());
		assertEquals("A", message[0]);

		new Object() {
			private Runnable remover;

			private void run()
			{
				remover = eventManager.register(Object.class, event -> {
					message[0] += "B";
					remover.run();
				});
			}
		}.run();

		eventManager.fire(new Object());
		assertEquals("AAB", message[0]);

		new Object() {
			private Runnable remover;

			private void run()
			{
				remover = eventManager.register(Object.class, new Consumer<Object>() {
					private boolean first = true;

					@Override
					public void accept(Object event)
					{
						message[0] += "C";

						if (first) {
							first = false;
						} else {
							remover.run();
						}
					}
				});
			}
		}.run();

		eventManager.fire(new Object());
		assertEquals("AABAC", message[0]);

		eventManager.fire(new Object());
		assertEquals("AABACAC", message[0]);

		eventManager.fire(new Object());
		assertEquals("AABACACA", message[0]);

		eventManager.fire(new Object());
		assertEquals("AABACACAA", message[0]);

	}

	@Test
	public void test5()
	{
		String[] message = {
			"",
		};

		IEventProvider<Object> eventManager = IEventProvider.createInstance();

		Runnable remover1 = eventManager.register(Object.class, event -> {
			message[0] += "A";
		});
		Runnable remover2 = eventManager.register(Object.class, event -> {
			message[0] += "B";
		});
		Runnable remover3 = eventManager.register(Object.class, event -> {
			message[0] += "C";
		});

		eventManager.fire(new Object());
		assertEquals("ABC", message[0]);

		message[0] = "";
		remover1.run();

		eventManager.fire(new Object());
		assertEquals("BC", message[0]);

		message[0] = "";
		remover2.run();

		eventManager.fire(new Object());
		assertEquals("C", message[0]);

		message[0] = "";
		remover3.run();

		eventManager.fire(new Object());
		assertEquals("", message[0]);

	}

}
