(defproject com.github.frankiesardo/icepick-parent "3.0.1-SNAPSHOT"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-sub "0.3.0"]]
  :deploy-repositories {"snapshots" {:url "https://clojars.org/repo"
                                     :username [:gpg :env/clojars_username]
                                     :password [:gpg :env/clojars_password]}}
  :sub ["icepick" "icepick-processor"])
