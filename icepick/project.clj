(defproject frankiesardo/icepick "3.0.1-SNAPSHOT"
  :description "Android instance state made easy"
  :url "https://github.com/frankiesardo/icepick"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :java-source-paths ["src"]
  :javac-options ["-target" "1.6" "-source" "1.6"]
  :profiles {:provided {:dependencies [[com.google.android/android "4.1.1.4"]]}
             :dev {:dependencies [[org.clojure/clojure "1.6.0"]]}})
