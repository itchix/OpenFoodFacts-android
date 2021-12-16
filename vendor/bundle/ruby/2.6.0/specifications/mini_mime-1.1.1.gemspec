# -*- encoding: utf-8 -*-
# stub: mini_mime 1.1.1 ruby lib

Gem::Specification.new do |s|
  s.name = "mini_mime".freeze
  s.version = "1.1.1"

  s.required_rubygems_version = Gem::Requirement.new(">= 0".freeze) if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib".freeze]
  s.authors = ["Sam Saffron".freeze]
  s.bindir = "exe".freeze
  s.date = "2021-08-23"
  s.description = "A lightweight mime type lookup toy".freeze
  s.email = ["sam.saffron@gmail.com".freeze]
  s.homepage = "https://github.com/discourse/mini_mime".freeze
  s.licenses = ["MIT".freeze]
  s.rubygems_version = "3.0.3".freeze
  s.summary = "A lightweight mime type lookup toy".freeze

  s.installed_by_version = "3.0.3" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<bundler>.freeze, [">= 0"])
      s.add_development_dependency(%q<rake>.freeze, [">= 0"])
      s.add_development_dependency(%q<minitest>.freeze, [">= 0"])
      s.add_development_dependency(%q<rubocop>.freeze, [">= 0"])
      s.add_development_dependency(%q<rubocop-discourse>.freeze, [">= 0"])
    else
      s.add_dependency(%q<bundler>.freeze, [">= 0"])
      s.add_dependency(%q<rake>.freeze, [">= 0"])
      s.add_dependency(%q<minitest>.freeze, [">= 0"])
      s.add_dependency(%q<rubocop>.freeze, [">= 0"])
      s.add_dependency(%q<rubocop-discourse>.freeze, [">= 0"])
    end
  else
    s.add_dependency(%q<bundler>.freeze, [">= 0"])
    s.add_dependency(%q<rake>.freeze, [">= 0"])
    s.add_dependency(%q<minitest>.freeze, [">= 0"])
    s.add_dependency(%q<rubocop>.freeze, [">= 0"])
    s.add_dependency(%q<rubocop-discourse>.freeze, [">= 0"])
  end
end
