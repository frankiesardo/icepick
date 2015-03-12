(defproject frankiesardo/icepick-processor "3.0.2"
  :description "Compile time processor for Icepick"
  :url "https://github.com/frankiesardo/icepick"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [frankiesardo/icepick "3.0.2"]
                 [com.google.auto.service/auto-service "1.0-rc2"]
                 [stencil "0.3.5"]]
  :deploy-repositories {"snapshots" {:url "https://clojars.org/repo"
                                     :username [:gpg :env/clojars_username]
                                     :password [:gpg :env/clojars_password]}
                        "releases" {:url "https://clojars.org/repo"}}
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.6" "-source" "1.6"]
  :aot :all
  :profiles {:dev {:dependencies [[com.google.testing.compile/compile-testing "0.4"]]}
             :provided {:dependencies [[com.google.android/android "4.1.1.4"]]}})
