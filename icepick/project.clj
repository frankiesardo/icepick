(defproject frankiesardo/icepick "3.2.1-SNAPSHOT"
  :description "Android instance state made easy"
  :url "https://github.com/frankiesardo/icepick"
  :license {:name "Eclipse Public License -v 1.0"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :java-source-paths ["src"]
  :javac-options ["-target" "1.6" "-source" "1.6"]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v"]
                  ["deploy" "clojars"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :jar-exclusions [#"project.clj"]
  :profiles {:provided {:dependencies [[com.google.android/android "4.1.1.4"]]}
             :dev      {:dependencies [[org.clojure/clojure "1.6.0"]]}})
