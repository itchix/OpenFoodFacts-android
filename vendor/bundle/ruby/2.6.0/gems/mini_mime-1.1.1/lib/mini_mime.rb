# frozen_string_literal: true
require "mini_mime/version"
require "thread"

module MiniMime
  def self.lookup_by_filename(filename)
    Db.lookup_by_filename(filename)
  end

  def self.lookup_by_extension(extension)
    Db.lookup_by_extension(extension)
  end

  def self.lookup_by_content_type(mime)
    Db.lookup_by_content_type(mime)
  end

  module Configuration
    class << self
      attr_accessor :ext_db_path
      attr_accessor :content_type_db_path
    end

    self.ext_db_path = File.expand_path("../db/ext_mime.db", __FILE__)
    self.content_type_db_path = File.expand_path("../db/content_type_mime.db", __FILE__)
  end

  class Info
    BINARY_ENCODINGS = %w(base64 8bit)

    attr_accessor :extension, :content_type, :encoding

    def initialize(buffer)
      @extension, @content_type, @encoding = buffer.split(/\s+/).map!(&:freeze)
    end

    def [](idx)
      if idx == 0
        @extension
      elsif idx == 1
        @content_type
      elsif idx == 2
        @encoding
      end
    end

    def binary?
      BINARY_ENCODINGS.include?(encoding)
    end
  end

  class Db
    LOCK = Mutex.new

    def self.lookup_by_filename(filename)
      extension = File.extname(filename)
      return if extension.empty?
      extension = extension[1..-1]
      lookup_by_extension(extension)
    end

    def self.lookup_by_extension(extension)
      LOCK.synchronize do
        @db ||= new
        @db.lookup_by_extension(extension) ||
          @db.lookup_by_extension(extension.downcase)
      end
    end

    def self.lookup_by_content_type(content_type)
      LOCK.synchronize do
        @db ||= new
        @db.lookup_by_content_type(content_type)
      end
    end

    class Cache
      def initialize(size)
        @size = size
        @hash = {}
      end

      def []=(key, val)
        rval = @hash[key] = val
        @hash.shift if @hash.length > @size
        rval
      end

      def fetch(key, &blk)
        @hash.fetch(key, &blk)
      end
    end

    class RandomAccessDb
      MAX_CACHED = 100

      def initialize(path, sort_order)
        @path = path
        @file = File.open(@path)

        @row_length = @file.readline.length
        @file_length = File.size(@path)
        @rows = @file_length / @row_length

        @hit_cache = Cache.new(MAX_CACHED)
        @miss_cache = Cache.new(MAX_CACHED)

        @sort_order = sort_order
      end

      def lookup(val)
        @hit_cache.fetch(val) do
          @miss_cache.fetch(val) do
            data = lookup_uncached(val)
            if data
              @hit_cache[val] = data
            else
              @miss_cache[val] = nil
            end

            data
          end
        end
      end

      # lifted from marcandre/backports
      def lookup_uncached(val)
        from = 0
        to = @rows - 1
        result = nil

        while from <= to do
          midpoint = from + (to - from).div(2)
          current = resolve(midpoint)
          data = current[@sort_order]
          if data > val
            to = midpoint - 1
          elsif data < val
            from = midpoint + 1
          else
            result = current
            break
          end
        end
        result
      end

      def resolve(row)
        @file.seek(row * @row_length)
        Info.new(@file.readline)
      end
    end

    def initialize
      @ext_db = RandomAccessDb.new(Configuration.ext_db_path, 0)
      @content_type_db = RandomAccessDb.new(Configuration.content_type_db_path, 1)
    end

    def lookup_by_extension(extension)
      @ext_db.lookup(extension)
    end

    def lookup_by_content_type(content_type)
      @content_type_db.lookup(content_type)
    end
  end
end
