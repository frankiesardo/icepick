package icepick;

import android.test.AndroidTestCase;

public class ViewInjectionTest extends AndroidTestCase {

  public void testNullFields() throws Exception {
    //JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n')
    //    .join("package test;", "import android.app.Activity;", "import android.view.View;",
    //        "import butterknife.InjectView;", "public class Test extends Activity {",
    //        "    @InjectView(1) View thing;", "}"));
    //
    //JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
    //    Joiner.on('\n')
    //        .join("package test;", "import android.view.View;",
    //            "import butterknife.ButterKnife.Finder;", "public class Test$$ViewInjector {",
    //            "  public static void inject(Finder finder, final test.Test target, Object source) {",
    //            "    View view;", "    view = finder.findById(source, 1);",
    //            "    if (view == null) {",
    //            "      throw new IllegalStateException(\"Required view with id '1' for field 'thing' was not found. If this view is optional add '@Optional' annotation.\");",
    //            "    }", "    target.thing = view;", "  }",
    //            "  public static void reset(test.Test target) {", "    target.thing = null;", "  }",
    //            "}"));
    //
    //ASSERT.about(javaSource()).that(source)
    //    .processedWith(butterknifeProcessors())
    //    .compilesWithoutError()
    //    .and()
    //    .generatesSources(expectedSource);
  }
}
