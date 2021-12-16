# -*- encoding: utf-8 -*-
# stub: fastimage 2.2.5 ruby lib

Gem::Specification.new do |s|
  s.name = "fastimage".freeze
  s.version = "2.2.5"

  s.required_rubygems_version = Gem::Requirement.new(">= 0".freeze) if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib".freeze]
  s.authors = ["Stephen Sykes".freeze]
  s.date = "2021-02-28"
  s.description = "FastImage finds the size or type of an image given its uri by fetching as little as needed.".freeze
  s.email = "sdsykes@gmail.com".freeze
  s.extra_rdoc_files = ["README.textile".freeze]
  s.files = ["README.textile".freeze]
  s.homepage = "http://github.com/sdsykes/fastimage".freeze
  s.licenses = ["MIT".freeze]
  s.rdoc_options = ["--charset=UTF-8".freeze]
  s.required_ruby_version = Gem::Requirement.new(">= 1.9.2".freeze)
  s.rubygems_version = "3.0.3".freeze
  s.summary = "FastImage - Image info fast".freeze

  s.installed_by_version = "3.0.3" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<fakeweb-fi>.freeze, ["~> 1.3"])
      s.add_development_dependency(%q<rake>.freeze, [">= 10.5"])
      s.add_development_dependency(%q<rdoc>.freeze, [">= 0"])
      s.add_development_dependency(%q<test-unit>.freeze, [">= 0"])
    else
      s.add_dependency(%q<fakeweb-fi>.freeze, ["~> 1.3"])
      s.add_dependency(%q<rake>.freeze, [">= 10.5"])
      s.add_dependency(%q<rdoc>.freeze, [">= 0"])
      s.add_dependency(%q<test-unit>.freeze, [">= 0"])
    end
  else
    s.add_dependency(%q<fakeweb-fi>.freeze, ["~> 1.3"])
    s.add_dependency(%q<rake>.freeze, [">= 10.5"])
    s.add_dependency(%q<rdoc>.freeze, [">= 0"])
    s.add_dependency(%q<test-unit>.freeze, [">= 0"])
  end
end
