(defproject frankiesardo/icepick-processor "3.2.1-SNAPSHOT"
  :description "Compile time processor for Icepick"
  :url "https://github.com/frankiesardo/icepick"
  :license {:name "Eclipse Public License -v 1.0"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [frankiesardo/icepick "3.2.1-SNAPSHOT"]
                 [com.google.auto.service/auto-service "1.0-rc2"]
                 [stencil "0.3.5"]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v"]
                  ["deploy" "clojars"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.6" "-source" "1.6"]
  :aot :all
  :profiles {:dev      {:dependencies [[com.google.testing.compile/compile-testing "0.4"]]}
             :provided {:dependencies [[com.google.android/android "4.1.1.4"]]}})
